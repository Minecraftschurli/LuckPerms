/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"); to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.luckperms.common.locale.message;

import me.lucko.luckperms.common.actionlog.LoggedAction;
import me.lucko.luckperms.common.plugin.AbstractLuckPermsPlugin;
import me.lucko.luckperms.common.plugin.LuckPermsPlugin;
import me.lucko.luckperms.common.sender.Sender;
import me.lucko.luckperms.common.util.DurationFormatter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.luckperms.api.context.Context;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.node.ChatMetaType;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.util.Tristate;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.TextComponent.join;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.Style.style;
import static net.kyori.adventure.text.format.TextDecoration.*;

/**
 * A collection of formatted messages used by the plugin.
 */
public interface Message {

    TextComponent OPEN_BRACKET = Component.text('(');
    TextComponent CLOSE_BRACKET = Component.text(')');
    TextComponent FULL_STOP = Component.text('.');

    Component PREFIX_COMPONENT = text()
            .color(GRAY)
            .append(text('['))
            .append(text()
                    .decoration(BOLD, true)
                    .append(text('L', AQUA))
                    .append(text('P', DARK_GREEN))
            )
            .append(text(']'))
            .build();

    static TextComponent prefixed(ComponentLike component) {
        return text()
                .append(PREFIX_COMPONENT)
                .append(space())
                .append(component)
                .build();
    }

    Args1<String> VIEW_AVAILABLE_COMMANDS_PROMPT = commandUsage -> prefixed(translatable()
            // "&3Use &a/{} help &3to view available commands."
            .key("commandsystem.available-commands")
            .color(DARK_AQUA)
            .args(text('/' + commandUsage, GREEN))
            .append(FULL_STOP)
    );

    Args0 NO_PERMISSION_FOR_SUBCOMMANDS = () -> prefixed(translatable()
            // "&3You do not have permission to use any sub commands."
            .key("commandsystem.no-permission-subcommands")
            .color(DARK_AQUA)
            .append(FULL_STOP)
    );

    Args2<String, String> FIRST_TIME_SETUP = (label, username) -> join(newline(),
            // "&3It seems that no permissions have been setup yet!"
            // "&3Before you can use any of the LuckPerms commands in-game, you need to use the console to give yourself access."
            // "&3Open your console and run:"
            // " &3&l> &a{} user {} permission set luckperms.* true"
            // "&3After you've done this, you can begin to define your permission assignments and groups."
            // "&3Don't know where to start? Check here: &7https://github.com/lucko/LuckPerms/wiki/Usage"
            prefixed(translatable()
                    .key("first-time.no-permissions-setup")
                    .color(DARK_AQUA)),
            prefixed(translatable()
                    .key("first-time.use-console-to-give-access")
                    .color(DARK_AQUA)
                    .append(FULL_STOP)),
            prefixed(translatable()
                    .key("first-time.console-command-prompt")
                    .color(DARK_AQUA)
                    .append(text(':'))),
            prefixed(text()
                    .color(DARK_AQUA)
                    .append(space())
                    .append(text('>', style(BOLD)))
                    .append(space())
                    .append(text(label + " user " + username + " permission set luckperms.* true", GREEN))
                    .append(newline())),
            prefixed(translatable()
                    .key("first-time.next-step")
                    .color(DARK_AQUA)
                    .append(FULL_STOP)),
            prefixed(translatable()
                    .key("first-time.wiki-prompt")
                    .color(DARK_AQUA)
                    .args(text("https://github.com/lucko/LuckPerms/wiki/Usage", GRAY)))
    );

    Args0 LOADING_DATABASE_ERROR = () -> prefixed(translatable()
            // "&cA database error occurred whilst loading permissions data. Please try again later. If you are a server admin, please check the console for any errors."
            .key("login.loading-database-error")
            .color(RED)
            .append(FULL_STOP)
            .append(space())
            .append(translatable("login.try-again"))
            .append(FULL_STOP)
            .append(space())
            .append(translatable("login.server-admin-check-console-errors"))
            .append(FULL_STOP)
    );

    Args0 LOADING_STATE_ERROR = () -> prefixed(translatable()
            // "&cPermissions data for your user was not loaded during the pre-login stage - unable to continue. Please try again later. If you are a server admin, please check the console for any errors."
            .key("login.data-not-loaded-at-pre")
            .color(RED)
            .append(text(" - "))
            .append(translatable("login.unable-to-continue"))
            .append(FULL_STOP)
            .append(space())
            .append(translatable("login.try-again"))
            .append(FULL_STOP)
            .append(space())
            .append(translatable("login.server-admin-check-console-errors"))
            .append(FULL_STOP)
    );

    Args0 LOADING_STATE_ERROR_CB_OFFLINE_MODE = () -> prefixed(translatable()
            // "&cPermissions data for your user was not loaded during the pre-login stage - this is likely due to a conflict between CraftBukkit and the online-mode setting. Please check the server console for more information."
            .key("login.data-not-loaded-at-pre")
            .color(RED)
            .append(text(" - "))
            .append(translatable("login.craftbukkit-offline-mode-error"))
            .append(FULL_STOP)
            .append(space())
            .append(translatable("login.server-admin-check-console-info"))
            .append(FULL_STOP)
    );

    Args0 LOADING_SETUP_ERROR = () -> prefixed(translatable()
            // "&cAn unexpected error occurred whilst setting up your permissions data. Please try again later."
            .key("login.unexpected-error")
            .color(RED)
            .append(FULL_STOP)
            .append(space())
            .append(translatable("login.try-again"))
            .append(FULL_STOP)
    );

    Args0 OP_DISABLED = () -> prefixed(translatable()
            // "&bThe vanilla OP system is disabled on this server."
            .key("opsystem.disabled")
            .color(AQUA)
            .append(FULL_STOP)
    );

    Args0 OP_DISABLED_SPONGE = () -> prefixed(translatable()
            // "&2Please note that Server Operator status has no effect on Sponge permission checks when a permission plugin is installed, you must edit user data directly."
            .key("opsystem.sponge-warning")
            .color(DARK_GREEN)
            .append(FULL_STOP)
    );

    Args1<LoggedAction> LOG = action -> join(newline(),
            // "&3LOG &3&l> &8(&e{}&8) [&a{}&8] (&b{}&8)"
            // "&3LOG &3&l> &f{}"
            prefixed(text()
                    .append(translatable("logs.actionlog-prefix", DARK_AQUA))
                    .append(space())
                    .append(text('>', DARK_AQUA, BOLD))
                    .append(space())
                    .append(text()
                            .color(DARK_GRAY)
                            .append(OPEN_BRACKET)
                            .append(text(action.getSourceFriendlyString(), YELLOW))
                            .append(CLOSE_BRACKET)
                    )
                    .append(space())
                    .append(text()
                            .color(DARK_GRAY)
                            .append(text('['))
                            .append(text(LoggedAction.getTypeCharacter(action.getTarget().getType()), GREEN))
                            .append(text(']'))
                    )
                    .append(space())
                    .append(text()
                            .color(DARK_GRAY)
                            .append(OPEN_BRACKET)
                            .append(text(action.getTargetFriendlyString(), AQUA))
                            .append(CLOSE_BRACKET)
                    )),
            prefixed(text()
                    .append(translatable("logs.actionlog-prefix", DARK_AQUA))
                    .append(space())
                    .append(text('>', DARK_AQUA, BOLD))
                    .append(space())
                    .append(text(action.getDescription(), WHITE)))
    );

    Args3<String, String, Tristate> VERBOSE_LOG_PERMISSION = (target, permission, result) -> prefixed(text()
            // "&3VB &3&l> &a{}&7 - &a{}&7 - {}"
            .append(translatable("logs.verbose-prefix", DARK_AQUA))
            .append(space())
            .append(text('>', DARK_AQUA, BOLD))
            .append(space())
            .append(text(target, GREEN))
            .append(text(" - ", GRAY))
            .append(text(permission, GREEN))
            .append(text(" - ", GRAY))
            .append(formatTristate(result))
    );

    Args3<String, String, String> VERBOSE_LOG_META = (target, metaKey, result) -> prefixed(text()
            // "&3VB &3&l> &a{}&7 - &bmeta: &a{}&7 - &7{}"
            .append(translatable("logs.verbose-prefix", DARK_AQUA))
            .append(space())
            .append(text('>', DARK_AQUA, BOLD))
            .append(space())
            .append(text(target, GREEN))
            .append(text(" - ", GRAY))
            .append(text("meta: ", BLUE))
            .append(text(metaKey, GREEN))
            .append(text(" - ", GRAY))
            .append(text(result, GRAY))
    );

    Args1<String> EXPORT_LOG = msg -> prefixed(text()
            // "&3EXPORT &3&l> &f{}"
            .append(translatable("logs.export-prefix", DARK_AQUA))
            .append(space())
            .append(text('>', DARK_AQUA, BOLD))
            .append(space())
            .append(text(msg, WHITE))
    );

    Args1<String> EXPORT_LOG_PROGRESS = msg -> prefixed(text()
            // "&3EXPORT &3&l> &7{}"
            .append(translatable("logs.export-prefix", DARK_AQUA))
            .append(space())
            .append(text('>', DARK_AQUA, BOLD))
            .append(space())
            .append(text(msg, GRAY))
    );

    Args2<String, String> MIGRATION_LOG = (pluginName, msg) -> prefixed(text()
            // "&3MIGRATION &7[&3{}&7] &3&l> &f{}"
            .append(translatable("logs.migration-prefix", DARK_AQUA))
            .append(space())
            .append(text()
                    .color(GRAY)
                    .append(text('['))
                    .append(text(pluginName, DARK_AQUA))
                    .append(text(']'))
            )
            .append(space())
            .append(text('>', DARK_AQUA, BOLD))
            .append(space())
            .append(text(msg, WHITE))
    );

    Args2<String, String> MIGRATION_LOG_PROGRESS = (pluginName, msg) -> prefixed(text()
            // "&3MIGRATION &7[&3{}&7] &3&l> &7{}"
            .append(translatable("logs.migration-prefix", DARK_AQUA))
            .append(space())
            .append(text()
                    .color(GRAY)
                    .append(text('['))
                    .append(text(pluginName, DARK_AQUA))
                    .append(text(']'))
            )
            .append(space())
            .append(text('>', DARK_AQUA, BOLD))
            .append(space())
            .append(text(msg, GRAY))
    );

    Args0 COMMAND_NOT_RECOGNISED = () -> prefixed(translatable()
            // "&cCommand not recognised."
            .key("commandsystem.command-not-recognised")
            .color(RED)
            .append(FULL_STOP)
    );

    Args0 COMMAND_NO_PERMISSION = () -> prefixed(translatable()
            // "&cYou do not have permission to use this command!"
            .key("commandsystem.no-permission")
            .color(RED)
    );

    Args2<String, String> MAIN_COMMAND_USAGE_HEADER2 = (name, usage) -> prefixed(text()
            // "&b{} Sub Commands: &7({} ...)"
            .color(AQUA)
            .append(text(name))
            .append(space())
            .append(translatable("commandsystem.usage.sub-commands-header"))
            .append(text(": "))
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(text(usage))
                    .append(text(" ..."))
                    .append(CLOSE_BRACKET)
            ));

    Args2<String, String> COMMAND_USAGE_DETAILED_HEADER = (name, usage) -> join(newline(),
            // "&3&lCommand Usage &3- &b{}"
            // "&b> &7{}"
            prefixed(text()
                    .append(translatable("commandsystem.usage.usage-header", DARK_AQUA, BOLD))
                    .append(text(" - ", DARK_AQUA))
                    .append(text(name, AQUA))),
            prefixed(text()
                    .append(text('>', AQUA))
                    .append(space())
                    .append(text(usage, GRAY)))
    );

    Args0 COMMAND_USAGE_DETAILED_ARGS_HEADER = () -> prefixed(translatable()
            .key("commandsystem.usage.arguments-header")
            .color(DARK_AQUA)
            .append(text(':'))
    );

    Args1<String> REQUIRED_ARGUMENT = name -> text()
            .color(DARK_GRAY)
            .append(text('<'))
            .append(text(name, GRAY))
            .append(text('>'))
            .build();

    Args1<String> OPTIONAL_ARGUMENT = name -> text()
            .color(DARK_GRAY)
            .append(text('['))
            .append(text(name, GRAY))
            .append(text(']'))
            .build();

    Args1<String> USER_NOT_ONLINE = id -> prefixed(translatable()
            // "&aUser &b{}&a is not online."
            .key("command.misc.loading.error.user-not-online")
            .color(GREEN)
            .args(text(id, AQUA))
            .append(FULL_STOP)
    );

    Args1<String> USER_NOT_FOUND = id -> prefixed(translatable()
            // "&cA user for &4{}&c could not be found."
            .key("command.misc.loading.error.user-not-found")
            .color(RED)
            .args(text(id, DARK_RED))
            .append(FULL_STOP)
    );

    Args1<String> GROUP_NOT_FOUND = id -> prefixed(translatable()
            // "&cA group named &4{}&c could not be found."
            .key("command.misc.loading.error.group-not-found")
            .color(RED)
            .args(text(id, DARK_RED))
            .append(FULL_STOP)
    );

    Args1<String> TRACK_NOT_FOUND = id -> prefixed(translatable()
            // "&cA track named &4{}&c could not be found."
            .key("command.misc.loading.error.track-not-found")
            .color(RED)
            .args(text(id, DARK_RED))
            .append(FULL_STOP)
    );

    Args1<String> USER_SAVE_ERROR = id -> prefixed(translatable()
            // "&cThere was an error whilst saving user data for &4{}&c."
            .key("command.misc.loading.error.user-save-error")
            .color(RED)
            .args(text(id, DARK_RED))
            .append(FULL_STOP)
    );

    Args1<String> GROUP_SAVE_ERROR = id -> prefixed(translatable()
            // "&cThere was an error whilst saving group data for &4{}&c."
            .key("command.misc.loading.error.group-save-error")
            .color(RED)
            .args(text(id, DARK_RED))
            .append(FULL_STOP)
    );

    Args1<String> TRACK_SAVE_ERROR = id -> prefixed(translatable()
            // "&cThere was an error whilst saving track data for &4{}&c."
            .key("command.misc.loading.error.track-save-error")
            .color(RED)
            .args(text(id, DARK_RED))
            .append(FULL_STOP)
    );

    Args1<String> USER_INVALID_ENTRY = invalid -> prefixed(translatable()
            // "&4{}&c is not a valid username/uuid."
            .key("command.misc.loading.error.user-invalid")
            .color(RED)
            .args(text(invalid, DARK_RED))
            .append(FULL_STOP)
    );

    Args1<String> GROUP_INVALID_ENTRY = invalid -> prefixed(translatable()
            // "&4{}&c is not a valid group name."
            .key("command.misc.loading.error.group-invalid")
            .color(RED)
            .args(text(invalid, DARK_RED))
            .append(FULL_STOP)
    );

    Args1<String> TRACK_INVALID_ENTRY = invalid -> prefixed(translatable()
            // "&4{}&c is not a valid track name."
            .key("command.misc.loading.error.track-invalid")
            .color(RED)
            .args(text(invalid, DARK_RED))
            .append(FULL_STOP)
    );

    Args2<String, String> VERBOSE_INVALID_FILTER = (invalid, error) -> prefixed(translatable()
            // "&4{}&c is not a valid verbose filter. &7({})"
            .key("command.verbose.invalid-filter")
            .color(RED)
            .args(text(invalid, DARK_RED))
            .append(FULL_STOP)
            .append(space())
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(text(error))
                    .append(CLOSE_BRACKET)
            )
    );

    Args0 VERBOSE_ON = () -> prefixed(translatable()
            // "&bVerbose logging &aenabled &bfor checks matching &aANY&b."
            .key("command.verbose.enabled")
            .color(AQUA)
            .args(translatable("command.verbose.enabled-term", GREEN), translatable("command.verbose.query-any", GREEN))
            .append(FULL_STOP)
    );

    Args1<String> VERBOSE_ON_QUERY = query -> prefixed(translatable()
            // "&bVerbose logging &aenabled &bfor checks matching &a{}&b."
            .key("command.verbose.enabled")
            .color(AQUA)
            .args(translatable("command.verbose.enabled-term", GREEN), text(query, GREEN))
            .append(FULL_STOP)
    );

    Args2<String, String> VERBOSE_ON_COMMAND = (user, command) -> prefixed(translatable()
            // "&bForcing &a{}&b to execute command &a/{}&b and reporting all checks made..."
            .key("command.verbose.command-exec")
            .color(AQUA)
            .args(text(user, GREEN), text(command, GREEN))
            .append(FULL_STOP)
    );

    Args0 VERBOSE_OFF = () -> prefixed(translatable()
            // "&bVerbose logging &cdisabled&b."
            .key("command.verbose.off")
            .color(AQUA)
            .args(translatable("command.verbose.disabled-term", RED))
            .append(FULL_STOP)
    );

    Args0 VERBOSE_OFF_COMMAND = () -> prefixed(translatable()
            // "&bCommand execution complete."
            .key("command.verbose.command-exec-complete")
            .color(AQUA)
            .append(FULL_STOP)
    );

    Args0 VERBOSE_RECORDING_ON = () -> prefixed(translatable()
            // "&bVerbose recording &aenabled &bfor checks matching &aANY&b."
            .key("command.verbose.enabled-recording")
            .color(AQUA)
            .args(translatable("command.verbose.enabled-term", GREEN), translatable("command.verbose.query-any", GREEN))
            .append(FULL_STOP)
    );

    Args1<String> VERBOSE_RECORDING_ON_QUERY = query -> prefixed(translatable()
            // "&bVerbose recording &aenabled &bfor checks matching &a{}&b."
            .key("command.verbose.enabled-recording")
            .color(AQUA)
            .args(translatable("command.verbose.enabled-term", GREEN), text(query, GREEN))
            .append(FULL_STOP)
    );

    Args0 VERBOSE_UPLOAD_START = () -> prefixed(translatable()
            // "&bVerbose logging &cdisabled&b, uploading results..."
            .key("command.verbose.uploading")
            .color(AQUA)
            .args(translatable("command.verbose.disabled-term", RED))
    );

    Args0 VERBOSE_RESULTS_URL = () -> prefixed(translatable()
            // "&aVerbose results URL:"
            .key("command.verbose.url")
            .color(GREEN)
            .append(text(':'))
    );

    Args0 TREE_UPLOAD_START = () -> prefixed(translatable()
            // "&bGenerating permission tree, please wait..."
            .key("command.tree.start")
            .color(AQUA)
    );

    Args0 TREE_EMPTY = () -> prefixed(translatable()
            // "&cUnable to generate tree, no results were found."
            .key("command.tree.empty")
            .color(RED)
            .append(FULL_STOP)
    );

    Args0 TREE_URL = () -> prefixed(translatable()
            // "&aPermission tree URL:"
            .key("command.tree.url")
            .color(GREEN)
            .append(text(':'))
    );

    Args2<Integer, String> GENERIC_HTTP_REQUEST_FAILURE = (code, message) -> prefixed(text()
            // "&cUnable to communicate with the web app. (response code &4{}&c, message='{}')"
            .color(RED)
            .append(translatable("command.misc.webapp-unable-to-communicate"))
            .append(FULL_STOP)
            .append(space())
            .append(text()
                    .append(OPEN_BRACKET)
                    .append(translatable("command.misc.response-code-key"))
                    .append(space())
                    .append(text(code))
                    .append(text(", "))
                    .append(translatable("command.misc.error-message-key"))
                    .append(text("='"))
                    .append(text(message))
                    .append(text("'"))
                    .append(CLOSE_BRACKET)
            )
    );

    Args0 GENERIC_HTTP_UNKNOWN_FAILURE = () -> prefixed(text()
            // "&cUnable to communicate with the web app. Check the console for errors."
            .color(RED)
            .append(translatable("command.misc.webapp-unable-to-communicate"))
            .append(FULL_STOP)
            .append(space())
            .append(translatable("command.misc.check-console-for-errors"))
            .append(FULL_STOP)
    );

    Args1<String> SEARCH_SEARCHING = searchQuery -> prefixed(translatable()
            // "&aSearching for users and groups with &bpermissions {}&a..."
            .key("command.search.searching.permission")
            .color(GREEN)
            .args(text("permissions " + searchQuery, AQUA))
            .append(text("..."))
    );

    Args1<String> SEARCH_SEARCHING_MEMBERS = group -> prefixed(translatable()
            // "&aSearching for users and groups who inherit from &b{}&a..."
            .key("command.search.searching.inherit")
            .color(GREEN)
            .args(text(group, AQUA))
            .append(text("..."))
    );

    Args0 SEARCH_RESULT_GROUP_DEFAULT = () -> prefixed(translatable()
            // "&7Note: when searching for members of the default group, offline players with no other permissions will not be shown!"
            .key("command.search.result.default-notice")
            .color(GRAY)
    );

    Args3<Integer, Integer, Integer> SEARCH_RESULT = (entries, users, groups) -> prefixed(translatable()
            // "&aFound &b{}&a entries from &b{}&a users and &b{}&a groups."
            .key("command.search.result")
            .color(GREEN)
            .args(text(entries, AQUA), text(users, AQUA), text(groups, AQUA))
            .append(FULL_STOP)
    );

    Args3<Integer, Integer, Integer> SEARCH_SHOWING_USERS = (page, totalPages, totalEntries) -> prefixed(text()
            // "&bShowing user entries:    &7(page &f{}&7 of &f{}&7 - &f{}&7 entries)"
            .color(AQUA)
            .append(translatable("command.search.showing-users"))
            .append(text(':'))
            .append(text("    "))
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(translatable()
                            .key("command.misc.page")
                            .args(text(page, WHITE), text(totalPages, WHITE))
                    )
                    .append(text(" - "))
                    .append(translatable()
                            .key("command.misc.page-entries")
                            .args(text(totalEntries, WHITE))
                    )
                    .append(CLOSE_BRACKET)
            )
    );

    Args3<Integer, Integer, Integer> SEARCH_SHOWING_GROUPS = (page, totalPages, totalEntries) -> prefixed(text()
            // "&bShowing group entries:    &7(page &f{}&7 of &f{}&7 - &f{}&7 entries)"
            .color(AQUA)
            .append(translatable("command.search.showing-groups"))
            .append(text(':'))
            .append(text("    "))
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(translatable()
                            .key("command.misc.page")
                            .args(text(page, WHITE), text(totalPages, WHITE))
                    )
                    .append(text(" - "))
                    .append(translatable()
                            .key("command.misc.page-entries")
                            .args(text(totalEntries, WHITE))
                    )
                    .append(CLOSE_BRACKET)
            )
    );

    Args1<String> APPLY_EDITS_INVALID_CODE = code -> prefixed(text()
            // "&cInvalid code. &7({})"
            .color(RED)
            .append(translatable("command.misc.invalid-code"))
            .append(FULL_STOP)
            .append(space())
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(text(code))
                    .append(CLOSE_BRACKET)
            )
    );

    Args1<String> APPLY_EDITS_UNABLE_TO_READ = invalid -> prefixed(translatable()
            // "&cUnable to read data using the given code. &7({})"
            .key("command.editor.apply-edits.unable-to-read")
            .color(RED)
            .append(FULL_STOP)
            .append(space())
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(text(invalid))
                    .append(CLOSE_BRACKET)
            )
    );

    Args1<String> APPLY_EDITS_UNKNOWN_TYPE = invalid -> prefixed(translatable()
            // "&cUnable to apply edit to the specified object type. &7({})"
            .key("command.editor.apply-edits.unknown-type")
            .color(RED)
            .append(FULL_STOP)
            .append(space())
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(text(invalid))
                    .append(CLOSE_BRACKET)
            )
    );

    Args1<String> APPLY_EDITS_TARGET_USER_NOT_UUID = target -> prefixed(translatable()
            // "&cTarget user &4{}&c is not a valid uuid."
            .key("command.misc.loading.error.user-not-uuid")
            .color(RED)
            .args(text(target, DARK_RED))
            .append(FULL_STOP)
    );

    Args1<String> APPLY_EDITS_TARGET_USER_UNABLE_TO_LOAD = target -> prefixed(translatable()
            // "&cUnable to load target user &4{}&c."
            .key("command.misc.loading.error.user-specific")
            .color(RED)
            .args(text(target, DARK_RED))
            .append(FULL_STOP)
    );

    Args0 APPLY_EDITS_TARGET_NO_CHANGES_PRESENT = () -> prefixed(translatable()
            // "&aNo changes were applied from the web editor. The returned data didn't contain any edits."
            .key("command.editor.apply-edits.no-changes")
            .color(GREEN)
            .append(FULL_STOP)
    );

    Args2<String, String> APPLY_EDITS_SUCCESS = (type, name) -> prefixed(translatable()
            // "&aWeb editor data was applied to {} &b{}&a successfully."
            .key("command.editor.apply-edits.success")
            .color(GREEN)
            .args(text(type), text(name, AQUA))
            .append(FULL_STOP)
    );

    Args2<Integer, Integer> APPLY_EDITS_SUCCESS_SUMMARY = (additions, deletions) -> prefixed(text()
            // "&7(&a{} &7{} and &c{} &7{})"
            .color(GRAY)
            .append(OPEN_BRACKET)
            .append(translatable()
                    .key("command.editor.apply-edits.success-summary")
                    .args(
                            text(additions, GREEN),
                            additions == 1 ?
                                    translatable("command.editor.apply-edits.success.additions-singular") :
                                    translatable("command.editor.apply-edits.success.additions"),
                            text(deletions, RED),
                            deletions == 1 ?
                                    translatable("command.editor.apply-edits.success.deletions-singular") :
                                    translatable("command.editor.apply-edits.success.deletions")
                    )
            )
            .append(CLOSE_BRACKET)
    );

    Args1<Node> APPLY_EDIT_NODE = node -> text()
            // "&f{} {} {} {}"
            .color(WHITE)
            .append(text(node.getKey()))
            .append(space())
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(text(node.getValue(), node.getValue() ? GREEN : RED))
                    .append(CLOSE_BRACKET)
            )
            .apply(builder -> {
                if (!node.getContexts().isEmpty()) {
                    builder.append(space());
                    builder.append(formatContextSetBracketed(node.getContexts(), empty()));
                }
            })
            .apply(builder -> {
                if (node.hasExpiry()) {
                    builder.append(space());
                    builder.append(text()
                            .color(GRAY)
                            .append(OPEN_BRACKET)
                            .append(text(DurationFormatter.CONCISE.format(node.getExpiryDuration())))
                            .append(CLOSE_BRACKET)
                    );
                }
            })
            .build();

    Args1<Node> APPLY_EDITS_DIFF_ADDED = node -> text()
            // "&a+  {}"
            .append(text('+', GREEN))
            .append(text("  "))
            .append(APPLY_EDIT_NODE.build(node))
            .build();

    Args1<Node> APPLY_EDITS_DIFF_REMOVED = node -> text()
            // "&c-  {}"
            .append(text('-', RED))
            .append(text("  "))
            .append(APPLY_EDIT_NODE.build(node))
            .build();

    Args0 EDITOR_NO_MATCH = () -> prefixed(translatable()
            // "&cUnable to open editor, no objects matched the desired type."
            .color(RED)
            .key("command.editor.no-match")
            .append(FULL_STOP)
    );

    Args0 EDITOR_START = () -> prefixed(translatable()
            // "&7Preparing a new editor session, please wait..."
            .color(GRAY)
            .key("command.editor.start")
    );

    Args0 EDITOR_URL = () -> prefixed(translatable()
            // "&aClick the link below to open the editor:"
            .color(GREEN)
            .key("command.editor.url")
            .append(text(':'))
    );

    Args2<Integer, String> EDITOR_HTTP_REQUEST_FAILURE = (code, message) -> prefixed(text()
            // "&cUnable to communicate with the editor. (response code &4{}&c, message='{}')"
            .color(RED)
            .append(translatable("command.editor.unable-to-communicate"))
            .append(FULL_STOP)
            .append(space())
            .append(text()
                    .append(OPEN_BRACKET)
                    .append(translatable("command.misc.response-code-key"))
                    .append(space())
                    .append(text(code))
                    .append(text(", "))
                    .append(translatable("command.misc.error-message-key"))
                    .append(text("='"))
                    .append(text(message))
                    .append(text("'"))
                    .append(CLOSE_BRACKET)
            )
    );

    Args0 EDITOR_HTTP_UNKNOWN_FAILURE = () -> prefixed(text()
            // "&cUnable to communicate with the editor. Check the console for errors."
            .color(RED)
            .append(translatable("command.editor.unable-to-communicate"))
            .append(FULL_STOP)
            .append(space())
            .append(translatable("command.misc.check-console-for-errors"))
            .append(FULL_STOP)
    );

    Args3<String, String, Tristate> CHECK_RESULT = (user, permission, result) -> prefixed(translatable()
            // "&aPermission check result on user &b{}&a for permission &b{}&a: &f{}"
            .color(GREEN)
            .key("command.check.result")
            .args(text(user, AQUA), text(permission, AQUA))
            .append(text(": "))
            .append(formatTristate(result))
    );

    Args1<String> CREATE_SUCCESS = name -> prefixed(translatable()
            // "&b{}&a was successfully created."
            .color(GREEN)
            .key("command.generic.create.success")
            .args(text(name, AQUA))
            .append(FULL_STOP)
    );

    Args1<String> DELETE_SUCCESS = name -> prefixed(translatable()
            // "&b{}&a was successfully deleted."
            .color(GREEN)
            .key("command.generic.delete.success")
            .args(text(name, AQUA))
            .append(FULL_STOP)
    );

    Args2<String, String> RENAME_SUCCESS = (from, to) -> prefixed(translatable()
            // "&b{}&a was successfully renamed to &b{}&a."
            .color(GREEN)
            .key("command.generic.rename.success")
            .args(text(from, AQUA), text(to, AQUA))
            .append(FULL_STOP)
    );

    Args2<String, String> CLONE_SUCCESS = (from, to) -> prefixed(translatable()
            // "&b{}&a was successfully cloned onto &b{}&a."
            .color(GREEN)
            .key("command.generic.clone.success")
            .args(text(from, AQUA), text(to, AQUA))
            .append(FULL_STOP)
    );

    Args3<String, String, ContextSet> ALREADY_INHERITS = (holder, group, context) -> prefixed(translatable()
            // "&b{}&a already inherits from &b{}&a in context {}&a."
            .color(GREEN)
            .key("command.generic.parent.already-inherits")
            .args(text(holder, AQUA), text(group, AQUA), formatContextSet(context))
            .append(FULL_STOP)
    );

    Args3<String, String, ContextSet> DOES_NOT_INHERIT = (holder, group, context) -> prefixed(translatable()
            // "&b{}&a does not inherit from &b{}&a in context {}&a."
            .color(GREEN)
            .key("command.generic.parent.doesnt-inherit")
            .args(text(holder, AQUA), text(group, AQUA), formatContextSet(context))
            .append(FULL_STOP)
    );

    Args3<String, String, ContextSet> ALREADY_TEMP_INHERITS = (holder, group, context) -> prefixed(translatable()
            // "&b{}&a already temporarily inherits from &b{}&a in context {}&a."
            .color(GREEN)
            .key("command.generic.parent.already-temp-inherits")
            .args(text(holder, AQUA), text(group, AQUA), formatContextSet(context))
            .append(FULL_STOP)
    );

    Args3<String, String, ContextSet> DOES_NOT_TEMP_INHERIT = (holder, group, context) -> prefixed(translatable()
            // "&b{}&a does not temporarily inherit from &b{}&a in context {}&a."
            .color(GREEN)
            .key("command.generic.parent.doesnt-temp-inherit")
            .args(text(holder, AQUA), text(group, AQUA), formatContextSet(context))
            .append(FULL_STOP)
    );

    Args2<String, String> TRACK_ALREADY_CONTAINS = (track, group) -> prefixed(translatable()
            // "&b{}&a already contains &b{}&a."
            .color(GREEN)
            .key("command.track.already-contains")
            .args(text(track, AQUA), text(group, AQUA))
            .append(FULL_STOP)
    );

    Args2<String, String> TRACK_DOES_NOT_CONTAIN = (track, group) -> prefixed(translatable()
            // "&b{}&a doesn't contain &b{}&a."
            .color(GREEN)
            .key("command.track.doesnt-contain")
            .args(text(track, AQUA), text(group, AQUA))
            .append(FULL_STOP)
    );

    Args1<String> TRACK_AMBIGUOUS_CALL = name -> prefixed(translatable()
            // "&4{}&c is a member of multiple groups on this track. Unable to determine their location."
            .color(RED)
            .key("command.track.error-multiple-groups")
            .args(text(name, DARK_RED))
            .append(FULL_STOP)
            .append(space())
            .append(translatable("command.track.error-ambiguous"))
            .append(FULL_STOP)
    );

    Args1<String> ALREADY_EXISTS = name -> prefixed(translatable()
            // "&4{}&c already exists!"
            .color(RED)
            .key("command.generic.create.error-already-exists")
            .args(text(name, DARK_RED))
            .append(FULL_STOP)
    );

    Args1<String> DOES_NOT_EXIST = name -> prefixed(translatable()
            // "&4{}&c does not exist!"
            .color(RED)
            .key("command.generic.delete.error-doesnt-exist")
            .args(text(name, DARK_RED))
            .append(FULL_STOP)
    );

    Args0 USER_LOAD_ERROR = () -> prefixed(translatable()
            // "&cAn unexpected error occurred. User not loaded."
            .color(RED)
            .key("command.misc.loading.error.unexpected")
            .append(FULL_STOP)
            .append(space())
            .append(translatable("command.misc.loading.error.user"))
            .append(FULL_STOP)
    );

    Args0 GROUP_LOAD_ERROR = () -> prefixed(translatable()
            // "&cAn unexpected error occurred. Group not loaded."
            .color(RED)
            .key("command.misc.loading.error.unexpected")
            .append(FULL_STOP)
            .append(space())
            .append(translatable("command.misc.loading.error.group"))
            .append(FULL_STOP)
    );

    Args0 GROUPS_LOAD_ERROR = () -> prefixed(translatable()
            // "&cAn unexpected error occurred. Unable to load all groups."
            .color(RED)
            .key("command.misc.loading.error.unexpected")
            .append(FULL_STOP)
            .append(space())
            .append(translatable("command.misc.loading.error.all-groups"))
            .append(FULL_STOP)
    );

    Args0 TRACK_LOAD_ERROR = () -> prefixed(translatable()
            // "&cAn unexpected error occurred. Track not loaded."
            .color(RED)
            .key("command.misc.loading.error.unexpected")
            .append(FULL_STOP)
            .append(space())
            .append(translatable("command.misc.loading.error.track"))
            .append(FULL_STOP)
    );

    Args0 TRACKS_LOAD_ERROR = () -> prefixed(translatable()
            // "&cAn unexpected error occurred. Unable to load all tracks."
            .color(RED)
            .key("command.misc.loading.error.unexpected")
            .append(FULL_STOP)
            .append(space())
            .append(translatable("command.misc.loading.error.all-tracks"))
            .append(FULL_STOP)
    );

    Args1<String> TRACK_EMPTY = name -> prefixed(translatable()
            // "&4{}&c cannot be used as it is empty or contains only one group."
            .color(RED)
            .key("command.track.error-empty")
            .args(text(name, DARK_RED))
            .append(FULL_STOP)
    );

    Args0 UPDATE_TASK_REQUEST = () -> prefixed(translatable()
            // "&bAn update task has been requested. Please wait..."
            .color(AQUA)
            .key("command.update-task.request")
            .append(FULL_STOP)
    );

    Args0 UPDATE_TASK_COMPLETE = () -> prefixed(translatable()
            // "&aUpdate task complete."
            .color(GREEN)
            .key("command.update-task.complete")
            .append(FULL_STOP)
    );

    Args0 UPDATE_TASK_COMPLETE_NETWORK = () -> prefixed(translatable()
            // "&aUpdate task complete. Now attempting to push to other servers."
            .color(GREEN)
            .key("command.update-task.complete")
            .append(FULL_STOP)
            .append(space())
            .append(translatable("command.update-task.push.attempting"))
            .append(FULL_STOP)
    );

    Args1<String> UPDATE_TASK_PUSH_SUCCESS = serviceName -> prefixed(translatable()
            // "&aOther servers were notified via &b{} Messaging &asuccessfully."
            .color(GREEN)
            .key("command.update-task.push.complete")
            .args(text(serviceName + " Messaging", AQUA))
            .append(FULL_STOP)
    );

    Args0 UPDATE_TASK_PUSH_FAILURE = () -> prefixed(translatable()
            // "&cError whilst pushing changes to other servers."
            .color(RED)
            .key("command.update-task.push.error")
            .append(FULL_STOP)
    );

    Args0 UPDATE_TASK_PUSH_FAILURE_NOT_SETUP = () -> prefixed(translatable()
            // "&cCannot push changes to other servers as a messaging service has not been configured."
            .color(RED)
            .key("command.update-task.push.error-not-setup")
            .append(FULL_STOP)
    );

    Args0 RELOAD_CONFIG_SUCCESS = () -> prefixed(translatable()
            // "&aThe configuration file was reloaded. &7(some options will only apply after the server has restarted)"
            .key("command.reload-config.success")
            .color(GREEN)
            .append(FULL_STOP)
            .append(space())
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(translatable("command.reload-config.restart-note"))
                    .append(CLOSE_BRACKET)
            )
    );

    Args2<LuckPermsPlugin, Map<String, String>> INFO = (plugin, storageMeta) -> join(newline(),
            // "&2Running &bLuckPerms v{}&2 by &bLuck&2."
            // "&f-  &3Platform: &f{}"
            // "&f-  &3Server Brand: &f{}"
            // "&f-  &3Server Version:"
            // "     &f{}"
            // "&f-  &bStorage:"
            // "     &3Type: &f{}"
            // "     &3Some meta value: {}"
            // "&f-  &3Extensions:"
            // "     &f{}"
            // "&f-  &bMessaging: &f{}"
            // "&f-  &bInstance:"
            // "     &3Static contexts: &f{}"
            // "     &3Online Players: &a{} &7(&a{}&7 unique)"
            // "     &3Uptime: &7{}"
            // "     &3Local Data: &a{} &7users, &a{} &7groups, &a{} &7tracks",
            prefixed(translatable()
                    .key("command.info.running-plugin")
                    .color(DARK_GREEN)
                    .append(space())
                    .append(text(AbstractLuckPermsPlugin.getPluginName(), AQUA))
                    .append(space())
                    .append(text("v" + plugin.getBootstrap().getVersion(), AQUA))
                    .append(text(" by "))
                    .append(text("Luck", AQUA))
                    .append(FULL_STOP)),
            prefixed(text()
                    .color(DARK_AQUA)
                    .append(text("-  ", WHITE))
                    .append(translatable("command.info.platform-key"))
                    .append(text(": "))
                    .append(text(plugin.getBootstrap().getType().getFriendlyName(), WHITE))),
            prefixed(text()
                    .color(DARK_AQUA)
                    .append(text("-  ", WHITE))
                    .append(translatable("command.info.server-brand-key"))
                    .append(text(": "))
                    .append(text(plugin.getBootstrap().getServerBrand(), WHITE))),
            prefixed(text()
                    .color(DARK_AQUA)
                    .append(text("-  ", WHITE))
                    .append(translatable("command.info.server-version-key"))
                    .append(text(':'))),
            prefixed(text()
                    .color(WHITE)
                    .append(text("     "))
                    .append(text(plugin.getBootstrap().getServerVersion()))),
            prefixed(text()
                    .color(AQUA)
                    .append(text("-  ", WHITE))
                    .append(translatable("command.info.storage-key"))
                    .append(text(':'))),
            prefixed(text()
                    .apply(builder -> {
                        builder.append(prefixed(text()
                                .color(DARK_AQUA)
                                .append(text("     "))
                                .append(translatable("command.info.storage-type-key"))
                                .append(text(": "))
                                .append(text(plugin.getStorage().getName(), WHITE))
                        ));

                        for (Map.Entry<String, String> metaEntry : storageMeta.entrySet()) {
                            builder.append(newline());

                            String value = metaEntry.getValue();
                            Component valueComponent;
                            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                                valueComponent = formatBoolean(Boolean.parseBoolean(value));
                            } else {
                                try {
                                    int i = Integer.parseInt(value);
                                    valueComponent = text(i, GREEN);
                                } catch (NumberFormatException e) {
                                    valueComponent = text(value, WHITE);
                                }
                            }

                            builder.append(prefixed(text()
                                    .color(DARK_AQUA)
                                    .append(text("     "))
                                    .append(text(metaEntry.getKey()))
                                    .append(text(": "))
                                    .append(valueComponent)
                            ));
                        }
                    })),
            prefixed(text()
                    .color(AQUA)
                    .append(text("-  ", WHITE))
                    .append(translatable("command.info.extensions-key"))
                    .append(text(':'))),
            prefixed(text()
                    .color(WHITE)
                    .append(text("     "))
                    .append(formatStringList(plugin.getExtensionManager().getLoadedExtensions().stream()
                            .map(e -> e.getClass().getSimpleName())
                            .collect(Collectors.toList())))),
            prefixed(text()
                    .color(AQUA)
                    .append(text("-  ", WHITE))
                    .append(translatable("command.info.messaging-key"))
                    .append(text(": "))
                    .append(plugin.getMessagingService().<Component>map(s -> text(s.getName(), WHITE)).orElse(translatable("command.misc.none", WHITE)))),
            prefixed(text()
                    .color(AQUA)
                    .append(text("-  ", WHITE))
                    .append(translatable("command.info.instance-key"))
                    .append(text(':'))),
            prefixed(text()
                    .color(DARK_AQUA)
                    .append(text("     "))
                    .append(translatable("command.info.static-contexts-key"))
                    .append(text(": "))
                    .append(formatContextSetBracketed(plugin.getContextManager().getStaticContext(), translatable("command.misc.none", WHITE)))),
            prefixed(text()
                    .color(DARK_AQUA)
                    .append(text("     "))
                    .append(translatable("command.info.online-players-key"))
                    .append(text(": "))
                    .append(text(plugin.getBootstrap().getPlayerCount(), WHITE))
                    .append(space())
                    .append(text()
                            .color(GRAY)
                            .append(OPEN_BRACKET)
                            .append(translatable()
                                    .key("command.info.online-players-unique")
                                    .args(text(plugin.getConnectionListener().getUniqueConnections().size(), GREEN))
                            )
                            .append(CLOSE_BRACKET)
                    )),
            prefixed(text()
                    .color(DARK_AQUA)
                    .append(text("     "))
                    .append(translatable("command.info.uptime-key"))
                    .append(text(": "))
                    .append(text(DurationFormatter.CONCISE_LOW_ACCURACY.format(Duration.between(plugin.getBootstrap().getStartupTime(), Instant.now())), GRAY))),
            prefixed(text()
                    .color(DARK_AQUA)
                    .append(text("     "))
                    .append(translatable("command.info.local-data-key"))
                    .append(text(": "))
                    .append(translatable()
                            .color(GRAY)
                            .args(
                                    text(plugin.getUserManager().getAll().size(), GREEN),
                                    text(plugin.getGroupManager().getAll().size(), GREEN),
                                    text(plugin.getTrackManager().getAll().size(), GREEN)
                            )
                    ))
    );

    Args1<String> CREATE_ERROR = name -> prefixed(translatable()
            // "&cThere was an error whilst creating &4{}&c."
            .key("command.generic.create.error")
            .color(RED)
            .args(text(name, DARK_RED))
            .append(FULL_STOP)
    );

    Args1<String> DELETE_ERROR = name -> prefixed(translatable()
            // "&cThere was an error whilst deleting &4{}&c."
            .key("command.generic.delete.error")
            .color(RED)
            .args(text(name, DARK_RED))
            .append(FULL_STOP)
    );

    Args0 DELETE_GROUP_ERROR_DEFAULT = () -> prefixed(translatable()
            // "&cYou cannot delete the default group."
            .key("command.group.delete.not-default")
            .color(RED)
            .append(FULL_STOP)
    );

    Args0 GROUPS_LIST = () -> prefixed(translatable()
            // "&aGroups: &7(name, weight, tracks)"
            .key("command.track.list.title")
            .color(GREEN)
            .append(text(": "))
            .append(text("(name, weight, tracks)", GRAY))
    );

    Args3<String, Integer, Collection<String>> GROUPS_LIST_ENTRY = (name, weight, tracks) -> prefixed(text()
            .append(text("-  ", WHITE))
            .append(text(name, DARK_AQUA))
            .append(text(" - ", GRAY))
            .append(text(weight, AQUA))
            .apply(builder -> {
                if (!tracks.isEmpty()) {
                    builder.append(text(" - ", GRAY));
                    builder.append(formatStringList(tracks));
                }
            })
    );

    Args1<Collection<String>> TRACKS_LIST = list -> prefixed(translatable()
            // "&aTracks: {}"
            .key("command.track.list.title")
            .color(GREEN)
            .append(text(": "))
            .append(formatStringList(list))
    );

    Args4<String, Integer, Integer, Integer> PERMISSION_INFO = (holder, page, totalPages, totalEntries) -> prefixed(text()
            // "&b{}'s Permissions:  &7(page &f{}&7 of &f{}&7 - &f{}&7 entries)"
            .color(AQUA)
            .append(translatable()
                    .key("command.generic.permission.info.title")
                    .args(text(holder))
            )
            .append(text(':'))
            .append(text("  "))
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(translatable()
                            .key("command.misc.page")
                            .args(text(page, WHITE), text(totalPages, WHITE))
                    )
                    .append(text(" - "))
                    .append(translatable()
                            .key("command.misc.page-entries")
                            .args(text(totalEntries, WHITE))
                    )
                    .append(CLOSE_BRACKET)
            )
    );

    Args1<String> PERMISSION_INFO_NO_DATA = holder -> prefixed(translatable()
            // "&b{}&a does not have any permissions set."
            .key("command.generic.permission.info.empty")
            .color(GREEN)
            .args(text(holder, AQUA))
            .append(FULL_STOP)
    );

    Args4<String, Integer, Integer, Integer> PARENT_INFO = (holder, page, totalPages, totalEntries) -> prefixed(text()
            // "&b{}'s Parents:  &7(page &f{}&7 of &f{}&7 - &f{}&7 entries)"
            .color(AQUA)
            .append(translatable()
                    .key("command.generic.parent.info.title")
                    .args(text(holder))
            )
            .append(text(':'))
            .append(text("  "))
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(translatable()
                            .key("command.misc.page")
                            .args(text(page, WHITE), text(totalPages, WHITE))
                    )
                    .append(text(" - "))
                    .append(translatable()
                            .key("command.misc.page-entries")
                            .args(text(totalEntries, WHITE))
                    )
                    .append(CLOSE_BRACKET)
            )
    );

    Args1<String> PARENT_INFO_NO_DATA = holder -> prefixed(translatable()
            // "&b{}&a does not have any parents defined."
            .key("command.generic.parent.info.empty")
            .color(GREEN)
            .args(text(holder, AQUA))
            .append(FULL_STOP)
    );

    Args1<String> LIST_TRACKS = holder -> prefixed(translatable()
            // "&b{}'s Tracks:"
            .key("command.generic.show-tracks.title")
            .color(AQUA)
            .args(text(holder))
            .append(text(':'))
    );

    Args2<String, Component> LIST_TRACKS_ENTRY = (name, path) -> text()
            // "&a{}: {}"
            .color(GREEN)
            .append(text(name))
            .append(text(": "))
            .append(path)
            .build();

    Args1<String> LIST_TRACKS_EMPTY = holder -> prefixed(translatable()
            // "&b{}&a is not on any tracks."
            .key("command.generic.show-tracks.empty")
            .color(GREEN)
            .args(text(holder, AQUA))
            .append(FULL_STOP)
    );

    Args4<String, String, Boolean, ContextSet> CHECK_PERMISSION = (holder, permission, value, context) -> prefixed(translatable()
            // "&b{}&a has permission &b{}&a set to {}&a in context {}&a."
            .key("command.generic.permission.check-inherits")
            .color(GREEN)
            .args(
                    text(permission, AQUA),
                    text(value, AQUA),
                    text(holder, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args5<String, String, Boolean, ContextSet, String> CHECK_INHERITS_PERMISSION = (holder, permission, value, context, inheritedFrom) -> prefixed(translatable()
            // "&b{}&a has permission &b{}&a set to {}&a in context {}&a. &7(inherited from &a{}&7)"
            .key("command.generic.permission.check-inherits")
            .color(GREEN)
            .args(
                    text(permission, AQUA),
                    text(value, AQUA),
                    text(holder, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
            .append(space())
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(translatable("command.generic.info.inherited-from"))
                    .append(space())
                    .append(text(inheritedFrom, GREEN))
                    .append(CLOSE_BRACKET)
            )
    );

    Args4<String, Boolean, String, ContextSet> SETPERMISSION_SUCCESS = (permission, value, holder, context) -> prefixed(translatable()
            // "&aSet &b{}&a to &b{}&a for &b{}&a in context {}&a."
            .key("command.generic.permission.set-temp")
            .color(GREEN)
            .args(
                    text(permission, AQUA),
                    text(value, AQUA),
                    text(holder, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args3<String, String, ContextSet> ALREADY_HASPERMISSION = (holder, permission, context) -> prefixed(translatable()
            // "&b{}&a already has &b{}&a set in context {}&a."
            .key("command.generic.permission.already-has")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text(permission, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args5<String, Boolean, String, Duration, ContextSet> SETPERMISSION_TEMP_SUCCESS = (permission, value, holder, duration, context) -> prefixed(translatable()
            // "&aSet &b{}&a to &b{}&a for &b{}&a for a duration of &b{}&a in context {}&a."
            .key("command.generic.permission.set-temp")
            .color(GREEN)
            .args(
                    text(permission, AQUA),
                    text(value, AQUA),
                    text(holder, AQUA),
                    text(DurationFormatter.LONG.format(duration), AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args3<String, String, ContextSet> ALREADY_HAS_TEMP_PERMISSION = (holder, permission, context) -> prefixed(translatable()
            // "&b{}&a already has &b{}&a set temporarily in context {}&a."
            .key("command.generic.permission.already-has-temp")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text(permission, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args3<String, String, ContextSet> UNSETPERMISSION_SUCCESS = (permission, holder, context) -> prefixed(translatable()
            // "&aUnset &b{}&a for &b{}&a in context {}&a."
            .key("command.generic.permission.unset")
            .color(GREEN)
            .args(
                    text(permission, AQUA),
                    text(holder, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args3<String, String, ContextSet> DOES_NOT_HAVE_PERMISSION = (holder, permission, context) -> prefixed(translatable()
            // "&b{}&a does not have &b{}&a set in context {}&a."
            .key("command.generic.permission.doesnt-have")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text(permission, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args3<String, String, ContextSet> UNSET_TEMP_PERMISSION_SUCCESS = (permission, holder, context) -> prefixed(translatable()
            // "&aUnset temporary permission &b{}&a for &b{}&a in context {}&a."
            .key("command.generic.permission.unset-temp")
            .color(GREEN)
            .args(
                    text(permission, AQUA),
                    text(holder, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args6<String, Boolean, String, Duration, ContextSet, Duration> UNSET_TEMP_PERMISSION_SUBTRACT_SUCCESS = (permission, value, holder, duration, context, durationLess) -> prefixed(translatable()
            // "&aSet &b{}&a to &b{}&a for &b{}&a for a duration of &b{}&a in context {}&a, &b{}&a less than before."
            .key("command.generic.permission.subtract")
            .color(GREEN)
            .args(
                    text(permission, AQUA),
                    text(value, AQUA),
                    text(holder, AQUA),
                    text(DurationFormatter.LONG.format(duration), AQUA),
                    formatContextSet(context),
                    text(DurationFormatter.LONG.format(durationLess), AQUA)
            )
            .append(FULL_STOP)
    );

    Args3<String, String, ContextSet> DOES_NOT_HAVE_TEMP_PERMISSION = (holder, permission, context) -> prefixed(translatable()
            // "&b{}&a does not have &b{}&a set temporarily in context {}&a."
            .key("command.generic.permission.doesnt-have-temp")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text(permission, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args3<String, String, ContextSet> SET_INHERIT_SUCCESS = (holder, parent, context) -> prefixed(translatable()
            // "&b{}&a now inherits permissions from &b{}&a in context {}&a."
            .key("command.generic.parent.add")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text(parent, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args4<String, String, Duration, ContextSet> SET_TEMP_INHERIT_SUCCESS = (holder, parent, duration, context) -> prefixed(translatable()
            // "&b{}&a now inherits permissions from &b{}&a for a duration of &b{}&a in context {}&a."
            .key("command.generic.parent.add-temp")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text(parent, AQUA),
                    text(DurationFormatter.LONG.format(duration), AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args3<String, String, ContextSet> SET_PARENT_SUCCESS = (holder, parent, context) -> prefixed(translatable()
            // "&b{}&a had their existing parent groups cleared, and now only inherits &b{}&a in context {}&a."
            .key("command.generic.parent.set")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text(parent, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args4<String, String, String, ContextSet> SET_TRACK_PARENT_SUCCESS = (holder, track, parent, context) -> prefixed(translatable()
            // "&b{}&a had their existing parent groups on track &b{}&a cleared, and now only inherits &b{}&a in context {}&a."
            .key("command.generic.parent.set-track")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text(track, AQUA),
                    text(parent, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args3<String, String, ContextSet> UNSET_INHERIT_SUCCESS = (holder, parent, context) -> prefixed(translatable()
            // "&b{}&a no longer inherits permissions from &b{}&a in context {}&a."
            .key("command.generic.parent.remove")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text(parent, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args3<String, String, ContextSet> UNSET_TEMP_INHERIT_SUCCESS = (holder, parent, context) -> prefixed(translatable()
            // "&b{}&a no longer temporarily inherits permissions from &b{}&a in context {}&a."
            .key("command.generic.parent.remove-temp")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text(parent, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args5<String, String, Duration, ContextSet, Duration> UNSET_TEMP_INHERIT_SUBTRACT_SUCCESS = (holder, parent, duration, context, durationLess) -> prefixed(translatable()
            // "&b{}&a will inherit permissions from &b{}&a for a duration of &b{}&a in context {}&a, &b{}&a less than before."
            .key("command.generic.parent.subtract")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text(parent, AQUA),
                    text(DurationFormatter.LONG.format(duration), AQUA),
                    formatContextSet(context),
                    text(DurationFormatter.LONG.format(durationLess), AQUA)
            )
            .append(FULL_STOP)
    );

    Args3<String, ContextSet, Integer> CLEAR_SUCCESS = (holder, context, removeCount) -> prefixed(translatable()
            // "&b{}&a's nodes were cleared in context {}&a. (&b{}&a nodes were removed.)"
            .key("command.generic.clear")
            .color(GREEN)
            .args(text(holder, AQUA), formatContextSet(context))
            .append(FULL_STOP)
            .append(space())
            .append(text()
                    .append(OPEN_BRACKET)
                    .append(translatable()
                            .key(removeCount == 1 ? "command.generic.clear.node-removed-singular" : "command.generic.clear.node-removed")
                            .args(text(removeCount))
                            .color(AQUA)
                            .append(FULL_STOP)
                    )
                    .append(CLOSE_BRACKET)
            )
    );

    Args3<String, ContextSet, Integer> PERMISSION_CLEAR_SUCCESS = (holder, context, removeCount) -> prefixed(translatable()
            // "&b{}&a's parents were cleared in context {}&a. (&b{}&a nodes were removed.)"
            .key("command.generic.permission.clear")
            .color(GREEN)
            .args(text(holder, AQUA), formatContextSet(context))
            .append(FULL_STOP)
            .append(space())
            .append(text()
                    .append(OPEN_BRACKET)
                    .append(translatable()
                            .key(removeCount == 1 ? "command.generic.clear.node-removed-singular" : "command.generic.clear.node-removed")
                            .args(text(removeCount))
                            .color(AQUA)
                            .append(FULL_STOP)
                    )
                    .append(CLOSE_BRACKET)
            )
    );

    Args3<String, ContextSet, Integer> PARENT_CLEAR_SUCCESS = (holder, context, removeCount) -> prefixed(translatable()
            // "&b{}&a's parents were cleared in context {}&a. (&b{}&a nodes were removed.)"
            .key("command.generic.parent.clear-track")
            .color(GREEN)
            .args(text(holder, AQUA), formatContextSet(context))
            .append(FULL_STOP)
            .append(space())
            .append(text()
                    .append(OPEN_BRACKET)
                    .append(translatable()
                            .key(removeCount == 1 ? "command.generic.clear.node-removed-singular" : "command.generic.clear.node-removed")
                            .args(text(removeCount))
                            .color(AQUA)
                            .append(FULL_STOP)
                    )
                    .append(CLOSE_BRACKET)
            )
    );

    Args4<String, String, ContextSet, Integer> PARENT_CLEAR_TRACK_SUCCESS = (holder, track, context, removeCount) -> prefixed(translatable()
            // "&b{}&a's parents on track &b{}&a were cleared in context {}&a. (&b{}&a nodes were removed.)"
            .key("command.generic.parent.clear-track")
            .color(GREEN)
            .args(text(holder, AQUA), text(track, AQUA), formatContextSet(context))
            .append(FULL_STOP)
            .append(space())
            .append(text()
                    .append(OPEN_BRACKET)
                    .append(translatable()
                            .key(removeCount == 1 ? "command.generic.clear.node-removed-singular" : "command.generic.clear.node-removed")
                            .args(text(removeCount))
                            .color(AQUA)
                            .append(FULL_STOP)
                    )
                    .append(CLOSE_BRACKET)
            )
    );

    Args4<String, String, ContextSet, Integer> META_CLEAR_SUCCESS = (holder, key, context, removeCount) -> prefixed(translatable()
            // "&b{}&a's meta matching type &b{}&a was cleared in context {}&a. (&b{}&a nodes were removed.)"
            .key("command.generic.meta.clear")
            .color(GREEN)
            .args(text(holder, AQUA), text(key, AQUA), formatContextSet(context))
            .append(FULL_STOP)
            .append(space())
            .append(text()
                    .append(OPEN_BRACKET)
                    .append(translatable()
                            .key(removeCount == 1 ? "command.generic.clear.node-removed-singular" : "command.generic.clear.node-removed")
                            .args(text(removeCount))
                            .color(AQUA)
                            .append(FULL_STOP)
                    )
                    .append(CLOSE_BRACKET)
            )
    );

    Args1<String> ILLEGAL_DATE_ERROR = invalid -> prefixed(translatable()
            // "&cCould not parse date &4{}&c."
            .key("command.misc.date-parse-error")
            .color(RED)
            .args(text(invalid, DARK_RED))
            .append(FULL_STOP)
    );

    Args0 PAST_DATE_ERROR = () -> prefixed(translatable()
            // "&cYou cannot set a date in the past!"
            .key("command.misc.date-in-past-error")
            .color(RED)
    );

    Args1<String> CHAT_META_PREFIX_HEADER = holder -> prefixed(translatable()
            // "&b{}'s Prefixes"
            .key("command.generic.chat-meta.info.title-prefix")
            .color(AQUA)
            .args(text(holder))
    );

    Args1<String> CHAT_META_SUFFIX_HEADER = holder -> prefixed(translatable()
            // "&b{}'s Suffixes"
            .key("command.generic.chat-meta.info.title-suffix")
            .color(AQUA)
            .args(text(holder))
    );

    Args1<String> META_HEADER = holder -> prefixed(translatable()
            // "&b{}'s Meta"
            .key("command.generic.meta.info.title")
            .color(AQUA)
            .args(text(holder))
    );

    Args4<Integer, String, String, ContextSet> CHAT_META_ENTRY = (priority, value, inheritedFrom, context) -> prefixed(text()
            // "&b-> {} &f- &f'{}&f' &8(&7inherited from &a{}&8) {}"
            .append(text("->", AQUA))
            .append(space())
            .append(text(priority, AQUA))
            .append(text(" - ", WHITE))
            .append(text().color(WHITE).append(text('\'')).append(text(value)).append(text('\'')))
            .append(space())
            .append(text()
                    .color(DARK_GRAY)
                    .append(OPEN_BRACKET)
                    .append(translatable()
                            .color(GRAY)
                            .key("command.generic.info.inherited-from")
                            .append(space())
                            .append(text(inheritedFrom, GREEN)))
                    .append(CLOSE_BRACKET)
            )
            .append(space())
            .append(formatContextSetBracketed(context, empty()))
    );

    Args4<String, String, String, ContextSet> META_ENTRY = (key, value, inheritedFrom, context) -> prefixed(text()
            // "&b-> &a{} &f= &f'{}&f' &8(&7inherited from &a{}&8) {}"
            .append(text("->", AQUA))
            .append(space())
            .append(text(key, GREEN))
            .append(text(" = ", WHITE))
            .append(text().color(WHITE).append(text('\'')).append(text(value)).append(text('\'')))
            .append(space())
            .append(text()
                    .color(DARK_GRAY)
                    .append(OPEN_BRACKET)
                    .append(translatable()
                            .color(GRAY)
                            .key("command.generic.info.inherited-from")
                            .append(space())
                            .append(text(inheritedFrom, GREEN)))
                    .append(CLOSE_BRACKET)
            )
            .append(space())
            .append(formatContextSetBracketed(context, empty()))
    );

    Args1<String> CHAT_META_PREFIX_NONE = holder -> prefixed(translatable()
            // "&b{} has no prefixes."
            .key("command.generic.chat-meta.info.none-prefix")
            .color(AQUA)
            .args(text(holder))
            .append(FULL_STOP)
    );

    Args1<String> CHAT_META_SUFFIX_NONE = holder -> prefixed(translatable()
            // "&b{} has no suffixes."
            .key("command.generic.chat-meta.info.none-suffix")
            .color(AQUA)
            .args(text(holder))
            .append(FULL_STOP)
    );

    Args1<String> META_NONE = holder -> prefixed(translatable()
            // "&b{} has no meta."
            .key("command.generic.meta.info.none")
            .color(AQUA)
            .args(text(holder))
            .append(FULL_STOP)
    );

    Args1<String> META_INVALID_PRIORITY = invalid -> prefixed(translatable()
            // "&cInvalid priority &4{}&c. Expected a number."
            .key("command.misc.invalid-priority")
            .color(RED)
            .args(text(invalid, DARK_RED))
            .append(FULL_STOP)
            .append(space())
            .append(translatable("command.misc.expected-number"))
            .append(FULL_STOP)
    );

    Args5<String, ChatMetaType, String, Integer, ContextSet> ALREADY_HAS_CHAT_META = (holder, type, value, priority, context) -> prefixed(translatable()
            // "&b{}&a already has {} &f'{}&f'&a set at a priority of &b{}&a in context {}&a."
            .key("command.generic.chat-meta.already-has")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text(type.toString()),
                    text().color(WHITE).append(text('\'')).append(text(value)).append(text('\'')),
                    text(priority, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args5<String, ChatMetaType, String, Integer, ContextSet> ALREADY_HAS_TEMP_CHAT_META = (holder, type, value, priority, context) -> prefixed(translatable()
            // "&b{}&a already has {} &f'{}&f'&a set temporarily at a priority of &b{}&a in context {}&a."
            .key("command.generic.chat-meta.already-has-temp")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text(type.toString()),
                    text().color(WHITE).append(text('\'')).append(text(value)).append(text('\'')),
                    text(priority, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args5<String, ChatMetaType, String, Integer, ContextSet> DOES_NOT_HAVE_CHAT_META = (holder, type, value, priority, context) -> prefixed(translatable()
            // "&b{}&a doesn't have {} &f'{}&f'&a set at a priority of &b{}&a in context {}&a."
            .key("command.generic.chat-meta.doesnt-have")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text(type.toString()),
                    text().color(WHITE).append(text('\'')).append(text(value)).append(text('\'')),
                    text(priority, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args5<String, ChatMetaType, String, Integer, ContextSet> DOES_NOT_HAVE_TEMP_CHAT_META = (holder, type, value, priority, context) -> prefixed(translatable()
            // "&b{}&a doesn't have {} &f'{}&f'&a set temporarily at a priority of &b{}&a in context {}&a."
            .key("command.generic.chat-meta.doesnt-have-temp")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text(type.toString()),
                    text().color(WHITE).append(text('\'')).append(text(value)).append(text('\'')),
                    text(priority, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args5<String, ChatMetaType, String, Integer, ContextSet> ADD_CHATMETA_SUCCESS = (holder, type, value, priority, context) -> prefixed(translatable()
            // "&b{}&a had {} &f'{}&f'&a set at a priority of &b{}&a in context {}&a."
            .key("command.generic.chat-meta.add")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text(type.toString()),
                    text().color(WHITE).append(text('\'')).append(text(value)).append(text('\'')),
                    text(priority, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args6<String, ChatMetaType, String, Integer, Duration, ContextSet> ADD_TEMP_CHATMETA_SUCCESS = (holder, type, value, priority, duration, context) -> prefixed(translatable()
            // "&b{}&a had {} &f'{}&f'&a set at a priority of &b{}&a for a duration of &b{}&a in context {}&a."
            .key("command.generic.chat-meta.add-temp")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text(type.toString()),
                    text().color(WHITE).append(text('\'')).append(text(value)).append(text('\'')),
                    text(priority, AQUA),
                    text(DurationFormatter.LONG.format(duration), AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args5<String, ChatMetaType, String, Integer, ContextSet> REMOVE_CHATMETA_SUCCESS = (holder, type, value, priority, context) -> prefixed(translatable()
            // "&b{}&a had {} &f'{}&f'&a at priority &b{}&a removed in context {}&a."
            .key("command.generic.chat-meta.remove")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text(type.toString()),
                    text().color(WHITE).append(text('\'')).append(text(value)).append(text('\'')),
                    text(priority, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args4<String, ChatMetaType, Integer, ContextSet> BULK_REMOVE_CHATMETA_SUCCESS = (holder, type, priority, context) -> prefixed(translatable()
            // "&b{}&a had all {}es at priority &b{}&a removed in context {}&a."
            .key("command.generic.chat-meta.remove-bulk")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text(type.toString() + "es"),
                    text(priority, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args5<String, ChatMetaType, String, Integer, ContextSet> REMOVE_TEMP_CHATMETA_SUCCESS = (holder, type, value, priority, context) -> prefixed(translatable()
            // "&b{}&a had temporary {} &f'{}&f'&a at priority &b{}&a removed in context {}&a."
            .key("command.generic.chat-meta.remove-temp")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text(type.toString()),
                    text().color(WHITE).append(text('\'')).append(text(value)).append(text('\'')),
                    text(priority, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args4<String, ChatMetaType, Integer, ContextSet> BULK_REMOVE_TEMP_CHATMETA_SUCCESS = (holder, type, priority, context) -> prefixed(translatable()
            // "&b{}&a had all temporary {}es at priority &b{}&a removed in context {}&a."
            .key("command.generic.chat-meta.remove-temp-bulk")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text(type.toString() + "es"),
                    text(priority, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args4<String, String, String, ContextSet> ALREADY_HAS_META = (holder, key, value, context) -> prefixed(translatable()
            // "&b{}&a already has meta key &f'{}&f'&a set to &f'{}&f'&a in context {}&a."
            .key("command.generic.meta.already-has")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text().color(WHITE).append(text('\'')).append(text(key)).append(text('\'')),
                    text().color(WHITE).append(text('\'')).append(text(value)).append(text('\'')),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args4<String, String, String, ContextSet> ALREADY_HAS_TEMP_META = (holder, key, value, context) -> prefixed(translatable()
            // "&b{}&a already has meta key &f'{}&f'&a temporarily set to &f'{}&f'&a in context {}&a."
            .key("command.generic.meta.already-has-temp")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text().color(WHITE).append(text('\'')).append(text(key)).append(text('\'')),
                    text().color(WHITE).append(text('\'')).append(text(value)).append(text('\'')),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args3<String, String, ContextSet> DOESNT_HAVE_META = (holder, key, context) -> prefixed(translatable()
            // "&b{}&a doesn't have meta key &f'{}&f'&a set in context {}&a."
            .key("command.generic.meta.doesnt-have")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text().color(WHITE).append(text('\'')).append(text(key)).append(text('\'')),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args3<String, String, ContextSet> DOESNT_HAVE_TEMP_META = (holder, key, context) -> prefixed(translatable()
            // "&b{}&a doesn't have meta key &f'{}&f'&a set temporarily in context {}&a."
            .key("command.generic.meta.doesnt-have-temp")
            .color(GREEN)
            .args(
                    text(holder, AQUA),
                    text().color(WHITE).append(text('\'')).append(text(key)).append(text('\'')),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args4<String, String, String, ContextSet> SET_META_SUCCESS = (key, value, holder, context) -> prefixed(translatable()
            // "&aSet meta key &f'{}&f'&a to &f'{}&f'&a for &b{}&a in context {}&a."
            .key("command.generic.meta.set")
            .color(GREEN)
            .args(
                    text().color(WHITE).append(text('\'')).append(text(key)).append(text('\'')),
                    text().color(WHITE).append(text('\'')).append(text(value)).append(text('\'')),
                    text(holder, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args5<String, String, String, Duration, ContextSet> SET_META_TEMP_SUCCESS = (key, value, holder, duration, context) -> prefixed(translatable()
            // "&aSet meta key &f'{}&f'&a to &f'{}&f'&a for &b{}&a for a duration of &b{}&a in context {}&a."
            .key("command.generic.meta.set-temp")
            .color(GREEN)
            .args(
                    text().color(WHITE).append(text('\'')).append(text(key)).append(text('\'')),
                    text().color(WHITE).append(text('\'')).append(text(value)).append(text('\'')),
                    text(holder, AQUA),
                    text(DurationFormatter.LONG.format(duration), AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args3<String, String, ContextSet> UNSET_META_SUCCESS = (key, holder, context) -> prefixed(translatable()
            // "&aUnset meta key &f'{}&f'&a for &b{}&a in context {}&a."
            .key("command.generic.meta.unset")
            .color(GREEN)
            .args(
                    text().color(WHITE).append(text('\'')).append(text(key)).append(text('\'')),
                    text(holder, AQUA),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args3<String, String, ContextSet> UNSET_META_TEMP_SUCCESS = (key, holder, context) -> prefixed(translatable()
            // "&aUnset temporary meta key &f'{}&f'&a for &b{}&a in context {}&a."
            .key("command.generic.meta.unset-temp")
            .color(GREEN)
            .args(
                    text().color(WHITE).append(text('\'')).append(text(key)).append(text('\'')),
                    text(holder, BLUE),
                    formatContextSet(context)
            )
            .append(FULL_STOP)
    );

    Args0 BULK_UPDATE_MUST_USE_CONSOLE = () -> prefixed(translatable()
            // "&cThe bulk update command can only be used from the console."
            .key("command.bulkupdate.must-use-console")
            .color(RED)
            .append(FULL_STOP)
    );

    Args0 BULK_UPDATE_INVALID_DATA_TYPE = () -> prefixed(translatable()
            // "&cInvalid type. Was expecting 'all', 'users' or 'groups'."
            .key("command.bulkupdate.invalid-data-type")
            .color(RED)
            .args(text("'all', 'users' or 'groups'"))
            .append(FULL_STOP)
    );

    Args1<String> BULK_UPDATE_INVALID_CONSTRAINT = invalid -> prefixed(translatable()
            // &cInvalid constraint &4{}&c. Constraints should be in the format '&f<field> <comparison operator> <value>&c'."
            .key("command.bulkupdate.invalid-constraint")
            .color(RED)
            .args(text(invalid, DARK_RED))
            .append(FULL_STOP)
            .append(space())
            .append(translatable()
                    .key("command.bulkupdate.invalid-constraint-format")
                    .args(text()
                            .append(text('\''))
                            .append(text("<field> <comparison operator> <value>", WHITE))
                            .append(text('\''))
                    )
                    .append(FULL_STOP))
    );

    Args1<String> BULK_UPDATE_INVALID_COMPARISON = invalid -> prefixed(translatable()
            // "&cInvalid comparison operator '&4{}&c'. Expected one of the following: &f==  !=  ~~  ~!"
            .key("command.bulkupdate.invalid-comparison")
            .color(RED)
            .args(text()
                    .append(text('\''))
                    .append(text(invalid, DARK_RED))
                    .append(text('\''))
            )
            .append(FULL_STOP)
            .append(space())
            .append(translatable()
                    .key("command.bulkupdate.invalid-comparison-format")
                    .color(WHITE)
                    .args(text("==  !=  ~~  ~!")))
    );

    Args1<String> BULK_UPDATE_QUEUED = id -> prefixed(translatable()
            // "&aBulk update operation was queued. &7(&f{}&7)"
            .key("command.bulkupdate.queued")
            .color(GREEN)
            .append(FULL_STOP)
            .append(space())
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(text(id, WHITE))
                    .append(CLOSE_BRACKET)
            )
    );

    Args2<String, String> BULK_UPDATE_CONFIRM = (label, id) -> prefixed(translatable()
            // "&aRun &b/{} bulkupdate confirm {} &ato execute the update."
            .key("command.bulkupdate.confirm")
            .color(GREEN)
            .args(text("/" + label + " bulkupdate confirm " + id, AQUA))
            .append(FULL_STOP)
    );

    Args1<String> BULK_UPDATE_UNKNOWN_ID = id -> prefixed(translatable()
            // "&aOperation with id &b{}&a does not exist or has expired."
            .key("command.bulkupdate.unknown-id")
            .color(GREEN)
            .args(text(id, AQUA))
            .append(FULL_STOP)
    );

    Args0 BULK_UPDATE_STARTING = () -> prefixed(translatable()
            // "&aRunning bulk update."
            .key("command.bulkupdate.starting")
            .color(GREEN)
            .append(FULL_STOP)
    );

    Args0 BULK_UPDATE_SUCCESS = () -> prefixed(translatable()
            // "&bBulk update completed successfully."
            .key("command.bulkupdate.success")
            .color(AQUA)
            .append(FULL_STOP)
    );

    Args0 BULK_UPDATE_FAILURE = () -> prefixed(translatable()
            // "&cBulk update failed, check the console for errors."
            .key("command.bulkupdate.failure")
            .color(RED)
            .append(FULL_STOP)
    );

    Args4<String, String, Boolean, Boolean> USER_INFO_GENERAL = (username, uuid, mojang, online) -> join(newline(),
            // "&b&l> &bUser Info: &f{}"
            // "&f- &3UUID: &f{}"
            // "&f    &7(type: {}&7)"
            // "&f- &3Status: {}"
            prefixed(text()
                    .color(AQUA)
                    .append(text('>', style(BOLD)))
                    .append(space())
                    .append(translatable("command.user.info.title"))
                    .append(text(": "))
                    .append(text(username, WHITE))),
            prefixed(text()
                    .color(DARK_AQUA)
                    .append(text("- ", WHITE))
                    .append(translatable("command.user.info.uuid-key"))
                    .append(text(": "))
                    .append(text(uuid, WHITE))),
            prefixed(text()
                    .color(GRAY)
                    .append(text("    "))
                    .append(OPEN_BRACKET)
                    .append(translatable("command.user.info.uuid-type-key"))
                    .append(text(": "))
                    .append(mojang ? translatable("command.user.info.uuid-type.mojang", DARK_GREEN) : translatable("command.user.info.uuid-type.not-mojang", DARK_GRAY))
                    .append(CLOSE_BRACKET)),
            prefixed(text()
                    .color(DARK_AQUA)
                    .append(text("- ", WHITE))
                    .append(translatable("command.user.info.status-key"))
                    .append(text(": "))
                    .append(online ? translatable("command.user.info.status.online", GREEN) : translatable("command.user.info.status.offline", RED)))
    );

    Args6<Boolean, ContextSet, Component, Component, String, Component> USER_INFO_CONTEXTUAL_DATA = (active, contexts, prefix, suffix, primaryGroup, meta) -> join(newline(),
            // "&f- &aContextual Data: &7(mode: {}&7)"
            // "    &3Contexts: {}"
            // "    &3Prefix: {}"
            // "    &3Suffix: {}"
            // "    &3Primary Group: &f{}"
            // "    &3Meta: {}"
            prefixed(text()
                    .append(text('-', WHITE))
                    .append(space())
                    .append(translatable("command.generic.contextual-data.title", GREEN))
                    .append(text(": ", GREEN))
                    .append(text()
                            .color(GRAY)
                            .append(OPEN_BRACKET)
                            .append(translatable("command.generic.contextual-data.mode.key"))
                            .append(text(": "))
                            .append(active ? translatable("command.generic.contextual-data.mode.active-player", DARK_GREEN) : translatable("command.generic.contextual-data.mode.server", DARK_GRAY))
                            .append(CLOSE_BRACKET)
                    )),
            prefixed(text()
                    .color(DARK_AQUA)
                    .append(text("    "))
                    .append(translatable("command.generic.contextual-data.contexts-key"))
                    .append(text(": "))
                    .append(formatContextSetBracketed(contexts, translatable("command.generic.contextual-data.null-result", AQUA)))),
            prefixed(text()
                    .color(DARK_AQUA)
                    .append(text("    "))
                    .append(translatable("command.generic.contextual-data.prefix-key"))
                    .append(text(": "))
                    .append(prefix)),
            prefixed(text()
                    .color(DARK_AQUA)
                    .append(text("    "))
                    .append(translatable("command.generic.contextual-data.suffix-key"))
                    .append(text(": "))
                    .append(suffix)),
            prefixed(text()
                    .color(DARK_AQUA)
                    .append(text("    "))
                    .append(translatable("command.generic.contextual-data.primary-group-key"))
                    .append(text(": "))
                    .append(text(primaryGroup, WHITE))),
            prefixed(text()
                    .color(DARK_AQUA)
                    .append(text("    "))
                    .append(translatable("command.generic.contextual-data.meta-key"))
                    .append(text(": "))
                    .append(meta))
    );

    Args0 INFO_PARENT_HEADER = () -> prefixed(text()
            .color(GREEN)
            .append(text("- ", WHITE))
            .append(translatable("command.generic.info.parent.title"))
            .append(text(':'))
    );

    Args0 INFO_TEMP_PARENT_HEADER = () -> prefixed(text()
            .color(GREEN)
            .append(text("- ", WHITE))
            .append(translatable("command.generic.info.parent.temporary-title"))
            .append(text(':'))
    );

    Args1<InheritanceNode> PARENT_NODE_ENTRY = node -> prefixed(text()
            .append(text("    >", DARK_AQUA))
            .append(space())
            .append(text(node.getGroupName(), WHITE))
            .append(space())
            .append(formatContextSetBracketed(node.getContexts(), empty()))
    );

    Args1<InheritanceNode> PARENT_TEMPORARY_NODE_ENTRY = node -> join(newline(),
            prefixed(text()
                    .append(text("    > ", DARK_AQUA))
                    .append(text(node.getGroupName(), WHITE))
                    .append(space())
                    .append(formatContextSetBracketed(node.getContexts(), empty()))),
            prefixed(text()
                    .color(DARK_GREEN)
                    .append(text("    -    "))
                    .append(translatable("command.generic.info.expires-in"))
                    .append(space())
                    .append(text(DurationFormatter.LONG.format(node.getExpiryDuration()))))
    );

    Args0 USER_REMOVEGROUP_ERROR_PRIMARY = () -> prefixed(translatable()
            // "&aYou cannot remove a user from their primary group."
            .key("command.user.removegroup.error-primary")
            .color(GREEN)
            .append(FULL_STOP)
    );

    Args2<String, String> USER_PRIMARYGROUP_SUCCESS = (user, group) -> prefixed(translatable()
            // "&b{}&a's primary group was set to &b{}&a."
            .key("command.user.primarygroup.set")
            .color(GREEN)
            .args(text(user, AQUA), text(group, AQUA))
            .append(FULL_STOP)
    );

    Args1<String> USER_PRIMARYGROUP_WARN_OPTION = option -> prefixed(translatable()
            // "&aWarning: The primary group calculation method being used by this server ({}) may not reflect this change."
            .key("command.user.primarygroup.warn-option")
            .color(GREEN)
            .args(text(option))
            .append(FULL_STOP)
    );

    Args2<String, String> USER_PRIMARYGROUP_ERROR_ALREADYHAS = (user, group) -> prefixed(translatable()
            // "&b{}&a already has &b{}&a set as their primary group."
            .key("command.user.primarygroup.already-has")
            .color(GREEN)
            .args(text(user, AQUA), text(group, AQUA))
            .append(FULL_STOP)
    );

    Args2<String, String> USER_PRIMARYGROUP_ERROR_NOTMEMBER = (user, group) -> prefixed(translatable()
            // "&b{}&a was not already a member of &b{}&a, adding them now."
            .key("command.user.primarygroup.not-member")
            .color(GREEN)
            .args(text(user, AQUA), text(group, AQUA))
            .append(FULL_STOP)
    );

    Args2<String, String> USER_TRACK_ERROR_NOT_CONTAIN_GROUP = (user, track) -> prefixed(translatable()
            // "&b{}&a isn't already in any groups on &b{}&a."
            .key("command.user.track.error-not-contain-group")
            .color(GREEN)
            .args(text(user, AQUA), text(track, AQUA))
            .append(FULL_STOP)
    );

    Args0 USER_TRACK_ERROR_AMBIGUOUS_TRACK_SELECTION = () -> prefixed(translatable()
            // "&cUnsure which track to use. Please specify it as an argument."
            .key("command.user.track.unsure-which-track")
            .color(RED)
            .append(FULL_STOP)
    );

    Args4<String, String, String, ContextSet> USER_TRACK_ADDED_TO_FIRST = (user, track, group, context) -> prefixed(translatable()
            // "&b{}&a isn't in any groups on &b{}&a, so they were added to the first group, &b{}&a in context {}&a."
            .key("command.user.promote.added-to-first")
            .color(GREEN)
            .args(text(user, AQUA), text(track, AQUA), text(group, AQUA), formatContextSet(context))
            .append(FULL_STOP)
    );

    Args2<String, String> USER_PROMOTE_NOT_ON_TRACK = (user, track) -> prefixed(translatable()
            // "&b{}&a isn't in any groups on &b{}&a, so was not promoted."
            .key("command.user.demote.end-of-track-not-removed")
            .color(GREEN)
            .args(text(user, AQUA), text(track, AQUA))
            .append(FULL_STOP)
    );

    Args5<String, String, String, String, ContextSet> USER_PROMOTE_SUCCESS = (user, track, from, to, context) -> prefixed(translatable()
            // "&aPromoting &b{}&a along track &b{}&a from &b{}&a to &b{}&a in context {}&a."
            .key("command.user.promote.success")
            .color(GREEN)
            .args(text(user, AQUA), text(track, AQUA), text(from, AQUA), text(to, AQUA), formatContextSet(context))
            .append(FULL_STOP)
    );

    Args2<String, String> USER_PROMOTE_ERROR_ENDOFTRACK = (track, user) -> prefixed(translatable()
            // "&aThe end of track &b{}&a was reached, unable to promote &b{}&a."
            .key("command.user.promote.end-of-track")
            .color(GREEN)
            .args(text(track, AQUA), text(user, AQUA))
            .append(FULL_STOP)
    );

    Args1<String> USER_PROMOTE_ERROR_MALFORMED = name -> join(newline(),
            // "&aThe next group on the track, &b{}&a, no longer exists. Unable to promote user."
            // "&aEither create the group, or remove it from the track and try again."
            prefixed(translatable()
                    .key("command.user.promote.next-group-deleted")
                    .color(GREEN)
                    .args(text(name, AQUA))
                    .append(FULL_STOP)
                    .append(space())
                    .append(translatable("command.user.promote.unable-to-promote"))
                    .append(FULL_STOP)),
            prefixed(translatable()
                    .key("command.user.track.missing-group-advice")
                    .color(GREEN)
                    .append(FULL_STOP))
    );

    Args5<String, String, String, String, ContextSet> USER_DEMOTE_SUCCESS = (user, track, from, to, context) -> prefixed(translatable()
            // "&aDemoting &b{}&a along track &b{}&a from &b{}&a to &b{}&a in context {}&a."
            .key("command.user.demote.success")
            .color(GREEN)
            .args(text(user, AQUA), text(track, AQUA), text(from, AQUA), text(to, AQUA), formatContextSet(context))
            .append(FULL_STOP)
    );

    Args3<String, String, String> USER_DEMOTE_ENDOFTRACK = (track, user, group) -> prefixed(translatable()
            // "&aThe end of track &b{}&a was reached, so &b{}&a was removed from &b{}&a."
            .key("command.user.demote.end-of-track")
            .color(GREEN)
            .args(text(track, AQUA), text(user, AQUA), text(group, AQUA))
            .append(FULL_STOP)
    );

    Args2<String, String> USER_DEMOTE_ENDOFTRACK_NOT_REMOVED = (track, user) -> prefixed(translatable()
            // "&aThe end of track &b{}&a was reached, but &b{}&a was not removed from the first group."
            .key("command.user.demote.end-of-track-not-removed")
            .color(GREEN)
            .args(text(track, AQUA), text(user, AQUA))
            .append(FULL_STOP)
    );

    Args1<String> USER_DEMOTE_ERROR_MALFORMED = name -> join(newline(),
            // "&aThe previous group on the track, &b{}&a, no longer exists. Unable to demote user."
            // "&aEither create the group, or remove it from the track and try again."
            prefixed(translatable()
                    .key("command.user.demote.previous-group-deleted")
                    .color(GREEN)
                    .args(text(name, AQUA))
                    .append(FULL_STOP)
                    .append(space())
                    .append(translatable("command.user.demote.unable-to-demote"))
                    .append(FULL_STOP)),
            prefixed(translatable()
                    .key("command.user.track.missing-group-advice")
                    .color(GREEN)
                    .append(FULL_STOP))
    );

    Args3<String, Component, Integer> GROUP_INFO_GENERAL = (name, displayName, weight) -> join(newline(),
            // "&b&l> &bGroup Info: &f{}"
            // "&f- &3Display Name: &f{}"
            // "&f- &3Weight: &f{}"
            prefixed(text()
                    .color(AQUA)
                    .append(text('>', style(BOLD)))
                    .append(space())
                    .append(translatable("command.group.info.title"))
                    .append(text(": "))
                    .append(text(name, WHITE))),
            prefixed(text()
                    .color(DARK_AQUA)
                    .append(text("- ", WHITE))
                    .append(translatable("command.group.info.display-name-key"))
                    .append(text(": "))
                    .append(displayName)),
            prefixed(text()
                    .color(DARK_AQUA)
                    .append(text("- ", WHITE))
                    .append(translatable("command.group.info.weight-key"))
                    .append(text(": "))
                    .append(text(weight, WHITE)))
    );

    Args3<Component, Component, Component> GROUP_INFO_CONTEXTUAL_DATA = (prefix, suffix, meta) -> join(newline(),
            // "&f- &aContextual Data: &7(mode: &8server&7)"
            // "    &3Prefix: {}"
            // "    &3Suffix: {}"
            // "    &3Meta: {}"
            prefixed(text()
                    .append(text('-', WHITE))
                    .append(space())
                    .append(translatable("command.generic.contextual-data.title", GREEN))
                    .append(text(": ", GREEN))
                    .append(text()
                            .color(GRAY)
                            .append(OPEN_BRACKET)
                            .append(translatable("command.generic.contextual-data.mode.key"))
                            .append(text(": "))
                            .append(translatable("command.generic.contextual-data.mode.server", DARK_GRAY))
                            .append(CLOSE_BRACKET)
                    )),
            prefixed(text()
                    .color(DARK_AQUA)
                    .append(text("    "))
                    .append(translatable("command.generic.contextual-data.prefix-key"))
                    .append(text(": "))
                    .append(prefix)),
            prefixed(text()
                    .color(DARK_AQUA)
                    .append(text("    "))
                    .append(translatable("command.generic.contextual-data.suffix-key"))
                    .append(text(": "))
                    .append(suffix)),
            prefixed(text()
                    .color(DARK_AQUA)
                    .append(text("    "))
                    .append(translatable("command.generic.contextual-data.meta-key"))
                    .append(text(": "))
                    .append(meta))
    );

    Args2<Integer, String> GROUP_SET_WEIGHT = (weight, group) -> prefixed(translatable()
            // "&aSet weight to &b{}&a for group &b{}&a."
            .key("command.group.setweight.set")
            .color(GREEN)
            .args(text(weight, AQUA), text(group, AQUA))
            .append(FULL_STOP)
    );

    Args1<String> GROUP_SET_DISPLAY_NAME_DOESNT_HAVE = group -> prefixed(translatable()
            // "&b{}&a doesn't have a display name set."
            .key("command.group.setdisplayname.doesnt-have")
            .color(GREEN)
            .args(text(group, AQUA))
            .append(FULL_STOP)
    );

    Args2<String, String> GROUP_SET_DISPLAY_NAME_ALREADY_HAS = (group, displayName) -> prefixed(translatable()
            // "&b{}&a already has a display name of &b{}&a."
            .key("command.group.setdisplayname.already-has")
            .color(GREEN)
            .args(text(group, AQUA), text(displayName, AQUA))
            .append(FULL_STOP)
    );

    Args2<String, String> GROUP_SET_DISPLAY_NAME_ALREADY_IN_USE = (displayName, group) -> prefixed(translatable()
            // "&aThe display name &b{}&a is already being used by &b{}&a."
            .key("command.group.setdisplayname.already-in-use")
            .color(GREEN)
            .args(text(displayName, AQUA), text(group, AQUA))
            .append(FULL_STOP)
    );

    Args3<String, String, ContextSet> GROUP_SET_DISPLAY_NAME = (displayName, group, context) -> prefixed(translatable()
            // "&aSet display name to &b{}&a for group &b{}&a in context {}&a."
            .key("command.group.setdisplayname.set")
            .color(GREEN)
            .args(text(displayName, AQUA), text(group, AQUA), formatContextSet(context))
            .append(FULL_STOP)
    );

    Args2<String, ContextSet> GROUP_SET_DISPLAY_NAME_REMOVED = (group, context) -> prefixed(translatable()
            // "&aRemoved display name for group &b{}&a in context {}&a."
            .key("command.group.setdisplayname.removed")
            .color(GREEN)
            .args(text(group, AQUA), formatContextSet(context))
            .append(FULL_STOP)
    );

    Args2<String, Component> TRACK_INFO = (name, path) -> join(newline(),
            // "&b&l> &bShowing Track: &f{}" + "\n" +
            // "&f- &7Path: &f{}",
            prefixed(text()
                    .color(AQUA)
                    .append(text('>', style(BOLD)))
                    .append(space())
                    .append(translatable("command.track.info.showing-track"))
                    .append(text(": "))
                    .append(text(name, WHITE))),
            prefixed(text()
                    .color(GRAY)
                    .append(text('-', WHITE))
                    .append(space())
                    .append(translatable("command.track.info.path-property"))
                    .append(text(": "))
                    .append(path))
    );

    Args1<String> TRACK_CLEAR = name -> prefixed(translatable()
            // "&b{}&a's groups track was cleared."
            .key("command.track.clear")
            .color(GREEN)
            .args(text(name, AQUA))
            .append(FULL_STOP)
    );

    Args2<String, String> TRACK_APPEND_SUCCESS = (group, track) -> prefixed(translatable()
            // "&aGroup &b{}&a was appended to track &b{}&a."
            .key("command.track.append.success")
            .color(GREEN)
            .args(text(group, AQUA), text(track, AQUA))
            .append(FULL_STOP)
    );

    Args3<String, String, Integer> TRACK_INSERT_SUCCESS = (group, track, position) -> prefixed(translatable()
            // "&aGroup &b{}&a was inserted into track &b{}&a at position &b{}&a."
            .key("command.track.insert.success")
            .color(GREEN)
            .args(text(group, AQUA), text(track, AQUA), text(position, AQUA))
            .append(FULL_STOP)
    );

    Args1<String> TRACK_INSERT_ERROR_NUMBER = invalid -> prefixed(translatable()
            // "&cExpected number but instead received: {}"
            .key("command.track.insert.error-number")
            .color(RED)
            .args(text(invalid))
            .append(FULL_STOP)
    );

    Args1<Integer> TRACK_INSERT_ERROR_INVALID_POS = position -> prefixed(translatable()
            // "&cUnable to insert at position &4{}&c. &7(invalid position)"
            .key("command.track.insert.error-invalid-pos")
            .color(RED)
            .args(text(position, DARK_RED))
            .append(FULL_STOP)
            .append(space())
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(translatable("command.track.insert.error-invalid-pos-reason"))
                    .append(CLOSE_BRACKET))
    );

    Args2<String, String> TRACK_REMOVE_SUCCESS = (group, track) -> prefixed(translatable()
            // "&aGroup &b{}&a was removed from track &b{}&a."
            .key("command.track.remove.success")
            .color(GREEN)
            .args(text(group, AQUA), text(track, AQUA))
            .append(FULL_STOP)
    );

    Args0 LOG_LOAD_ERROR = () -> prefixed(translatable()
            // "&cThe log could not be loaded."
            .key("command.log.load-error")
            .color(RED)
            .append(FULL_STOP)
    );

    Args1<Integer> LOG_INVALID_PAGE_RANGE = maxPage -> prefixed(text()
            // "&cInvalid page number. Please enter a value between &41&c and &4{}&c."
            .color(RED)
            .append(translatable("command.log.invalid-page"))
            .append(FULL_STOP)
            .append(space())
            .append(translatable()
                    .key("command.log.invalid-page-range")
                    .args(text(1, DARK_RED), text(maxPage, DARK_RED))
            )
            .append(FULL_STOP)
    );

    Args0 LOG_NO_ENTRIES = () -> prefixed(translatable()
            // "&bNo log entries to show."
            .key("command.log.empty")
            .color(AQUA)
            .append(FULL_STOP)
    );

    Args2<Integer, LoggedAction> LOG_ENTRY = (pos, action) -> join(newline(),
            // "&b#{} &8(&7{} ago&8) &8(&e{}&8) [&a{}&8] (&b{}&8)"
            // "&7> &f{}"
            prefixed(text()
                    .append(text("#" + pos, AQUA))
                    .append(space())
                    .append(text()
                            .color(DARK_GRAY)
                            .append(OPEN_BRACKET)
                            .append(text()
                                    .content(DurationFormatter.CONCISE_LOW_ACCURACY.format(action.getDurationSince()) + " ago")
                                    .color(GRAY)
                            )
                            .append(CLOSE_BRACKET)
                    )
                    .append(space())
                    .append(text()
                            .color(DARK_GRAY)
                            .append(OPEN_BRACKET)
                            .append(text(action.getSourceFriendlyString(), YELLOW))
                            .append(CLOSE_BRACKET)
                    )
                    .append(space())
                    .append(text()
                            .color(DARK_GRAY)
                            .append(text('['))
                            .append(text(LoggedAction.getTypeCharacter(action.getTarget().getType()), GREEN))
                            .append(text(']'))
                    )
                    .append(space())
                    .append(text()
                            .color(DARK_GRAY)
                            .append(OPEN_BRACKET)
                            .append(text(action.getTargetFriendlyString(), AQUA))
                            .append(CLOSE_BRACKET)
                    )),
            prefixed(text()
                    .append(text("> ", GRAY))
                    .append(text(action.getDescription(), WHITE)))
    );

    Args0 LOG_NOTIFY_CONSOLE = () -> prefixed(translatable()
            // "&cCannot toggle notifications for console."
            .key("command.log.notify.error-console")
            .color(RED)
            .append(FULL_STOP)
    );

    Args0 LOG_NOTIFY_TOGGLE_ON = () -> prefixed(translatable()
            // "&aEnabled&b logging output."
            .key("command.log.notify.changed-state")
            .color(AQUA)
            .args(translatable("command.log.notify.enabled-term", GREEN))
            .append(FULL_STOP)
    );

    Args0 LOG_NOTIFY_TOGGLE_OFF = () -> prefixed(translatable()
            // "&cDisabled&b logging output."
            .key("command.log.notify.changed-state")
            .color(AQUA)
            .args(translatable("command.log.notify.disabled-term", RED))
            .append(FULL_STOP)
    );

    Args0 LOG_NOTIFY_ALREADY_ON = () -> prefixed(translatable()
            // "&cYou are already receiving notifications."
            .key("command.log.notify.already-on")
            .color(RED)
            .append(FULL_STOP)
    );

    Args0 LOG_NOTIFY_ALREADY_OFF = () -> prefixed(translatable()
            // "&cYou aren't currently receiving notifications."
            .key("command.log.notify.already-off")
            .color(RED)
            .append(FULL_STOP)
    );

    Args0 LOG_NOTIFY_UNKNOWN = () -> prefixed(translatable()
            // "&cState unknown. Expecting \"on\" or \"off\"."
            .key("command.log.notify.invalid-state")
            .color(RED)
            .args(text("\"on\""), text("\"off\""))
            .append(FULL_STOP)
    );

    Args3<String, Integer, Integer> LOG_SEARCH_HEADER = (query, page, totalPages) -> prefixed(text()
            // "&aShowing recent actions for query &b{}  &7(page &f{}&7 of &f{}&7)"
            .color(GREEN)
            .append(translatable()
                    .key("command.log.show.search")
                    .args(text(query, AQUA))
            )
            .append(text("  "))
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(translatable()
                            .key("command.misc.page")
                            .args(text(page, WHITE), text(totalPages, WHITE))
                    )
                    .append(CLOSE_BRACKET)
            )
    );

    Args2<Integer, Integer> LOG_RECENT_HEADER = (page, totalPages) -> prefixed(text()
            // "&aShowing recent actions  &7(page &f{}&7 of &f{}&7)"
            .color(GREEN)
            .append(translatable("command.log.show.recent"))
            .append(text("  "))
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(translatable()
                            .key("command.misc.page")
                            .args(text(page, WHITE), text(totalPages, WHITE))
                    )
                    .append(CLOSE_BRACKET)
            )
    );

    Args3<String, Integer, Integer> LOG_RECENT_BY_HEADER = (name, page, totalPages) -> prefixed(text()
            // "&aShowing recent actions by &b{}  &7(page &f{}&7 of &f{}&7)"
            .color(GREEN)
            .append(translatable()
                    .key("command.log.show.by")
                    .args(text(name, AQUA))
            )
            .append(text("  "))
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(translatable()
                            .key("command.misc.page")
                            .args(text(page, WHITE), text(totalPages, WHITE))
                    )
                    .append(CLOSE_BRACKET)
            )
    );

    Args3<String, Integer, Integer> LOG_HISTORY_USER_HEADER = (name, page, totalPages) -> prefixed(text()
            // "&aShowing history for user &b{}  &7(page &f{}&7 of &f{}&7)"
            .color(GREEN)
            .append(translatable()
                    .key("command.log.show.history")
                    .args(text("user"), text(name, AQUA))
            )
            .append(text("  "))
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(translatable()
                            .key("command.misc.page")
                            .args(text(page, WHITE), text(totalPages, WHITE))
                    )
                    .append(CLOSE_BRACKET)
            )
    );

    Args3<String, Integer, Integer> LOG_HISTORY_GROUP_HEADER = (name, page, totalPages) -> prefixed(text()
            // "&aShowing history for group &b{}  &7(page &f{}&7 of &f{}&7)"
            .color(GREEN)
            .append(translatable()
                    .key("command.log.show.history")
                    .args(text("group"), text(name, AQUA))
            )
            .append(text("  "))
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(translatable()
                            .key("command.misc.page")
                            .args(text(page, WHITE), text(totalPages, WHITE))
                    )
                    .append(CLOSE_BRACKET)
            )
    );

    Args3<String, Integer, Integer> LOG_HISTORY_TRACK_HEADER = (name, page, totalPages) -> prefixed(text()
            // "&aShowing history for track &b{}  &7(page &f{}&7 of &f{}&7)"
            .color(GREEN)
            .append(translatable()
                    .key("command.log.show.history")
                    .args(text("track"), text(name, AQUA))
            )
            .append(text("  "))
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(translatable()
                            .key("command.misc.page")
                            .args(text(page, WHITE), text(totalPages, WHITE))
                    )
                    .append(CLOSE_BRACKET)
            )
    );

    Args0 IMPORT_ALREADY_RUNNING = () -> prefixed(text()
            // "&cAnother import process is already running. Please wait for it to finish and try again."
            .color(RED)
            .append(translatable("command.import.already-running"))
            .append(FULL_STOP)
            .append(space())
            .append(translatable("command.misc.wait-to-finish"))
            .append(FULL_STOP)
    );

    Args0 EXPORT_ALREADY_RUNNING = () -> prefixed(text()
            // "&cAnother export process is already running. Please wait for it to finish and try again."
            .color(RED)
            .append(translatable("command.export.already-running"))
            .append(FULL_STOP)
            .append(space())
            .append(translatable("command.misc.wait-to-finish"))
            .append(FULL_STOP)
    );

    Args1<String> FILE_NOT_WITHIN_DIRECTORY = file -> prefixed(text()
            // "&cError: File &4{}&c must be a direct child of the data directory."
            .color(RED)
            .append(translatable("command.import.error-term"))
            .append(text(": "))
            .append(translatable("command.misc.file-must-be-in-data", text(file, DARK_RED)))
            .append(FULL_STOP)
    );

    Args1<String> EXPORT_FILE_ALREADY_EXISTS = file -> prefixed(text()
            // "&cError: File &4{}&c already exists."
            .color(RED)
            .append(translatable("command.export.error-term"))
            .append(text(": "))
            .append(translatable("command.export.file.already-exists", text(file, DARK_RED)))
            .append(FULL_STOP)
    );

    Args1<String> EXPORT_FILE_NOT_WRITABLE = file -> prefixed(text()
            // "&cError: File &4{}&c is not writable."
            .color(RED)
            .append(translatable("command.export.error-term"))
            .append(text(": "))
            .append(translatable("command.export.file.not-writable", text(file, DARK_RED)))
            .append(FULL_STOP)
    );

    Args0 EXPORT_FILE_FAILURE = () -> prefixed(translatable()
            // "&cAn unexpected error occured whilst writing to the file."
            .key("command.export.file-unexpected-error-writing")
            .color(RED)
            .append(FULL_STOP)
    );

    Args1<String> EXPORT_FILE_SUCCESS = file -> prefixed(translatable()
            // "&aSuccessfully exported to &b{}&a."
            .key("command.export.file.success")
            .color(GREEN)
            .args(text(file, AQUA))
            .append(FULL_STOP)
    );

    Args2<String, String> EXPORT_WEB_SUCCESS = (pasteId, label) -> join(newline(),
            // "&aExport code: &7{}"
            // "&7Use the following command to import:"
            // "&a/{} import {} --upload"
            prefixed(text()
                    .color(GREEN)
                    .append(translatable("command.export.web.export-code"))
                    .append(text(": "))
                    .append(text(pasteId, GRAY))),
            translatable()
                    .key("command.export.web.import-command-description")
                    .color(GRAY)
                    .append(text(":")),
            text("/" + label + " import " + pasteId + " --upload", GREEN));

    Args1<String> IMPORT_FILE_DOESNT_EXIST = file -> prefixed(text()
            // "&cError: File &4{}&c does not exist."
            .color(RED)
            .append(translatable("command.import.error-term"))
            .append(text(": "))
            .append(translatable("command.import.file.doesnt-exist", text(file, DARK_RED)))
            .append(FULL_STOP)
    );

    Args1<String> IMPORT_FILE_NOT_READABLE = file -> prefixed(text()
            // "&cError: File &4{}&c is not readable."
            .color(RED)
            .append(translatable("command.import.error-term"))
            .append(text(": "))
            .append(translatable("command.import.file.not-readable", text(file, DARK_RED)))
            .append(FULL_STOP)
    );

    Args0 IMPORT_FILE_READ_FAILURE = () -> prefixed(text()
            // "&cAn unexpected error occured whilst reading from the import file. (is it the correct format?)"
            .color(RED)
            .append(translatable("command.import.file.unexpected-error-reading"))
            .append(FULL_STOP)
            .append(space())
            .append(text()
                    .append(OPEN_BRACKET)
                    .append(translatable("command.import.file.correct-format"))
                    .append(CLOSE_BRACKET)
            )
    );

    Args1<String> IMPORT_WEB_INVALID_CODE = code -> prefixed(text()
            // "&cInvalid code. &7({})"
            .color(RED)
            .append(translatable("command.misc.invalid-code"))
            .append(FULL_STOP)
            .append(space())
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(text(code))
                    .append(CLOSE_BRACKET)
            )
    );

    Args2<Integer, String> HTTP_REQUEST_FAILURE = (code, message) -> prefixed(text()
            // "&cUnable to communicate with bytebin. (response code &4{}&c, message='{}')"
            .color(RED)
            .append(translatable("command.misc.bytebin-unable-to-communicate"))
            .append(FULL_STOP)
            .append(space())
            .append(text()
                    .append(OPEN_BRACKET)
                    .append(translatable("command.misc.response-code-key"))
                    .append(space())
                    .append(text(code))
                    .append(text(", "))
                    .append(translatable("command.misc.error-message-key"))
                    .append(text("='"))
                    .append(text(message))
                    .append(text("'"))
                    .append(CLOSE_BRACKET)
            )
    );

    Args0 HTTP_UNKNOWN_FAILURE = () -> prefixed(text()
            // "&cUnable to communicate with bytebin. Check the console for errors."
            .color(RED)
            .append(translatable("command.misc.bytebin-unable-to-communicate"))
            .append(FULL_STOP)
            .append(space())
            .append(translatable("command.misc.check-console-for-errors"))
            .append(FULL_STOP)
    );

    Args1<String> IMPORT_UNABLE_TO_READ = code -> prefixed(text()
            // "&cUnable to read data using the given code. &7({})"
            .color(RED)
            .append(translatable("command.import.web.unable-to-read"))
            .append(FULL_STOP)
            .append(space())
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(text(code))
                    .append(CLOSE_BRACKET)
            )
    );

    Args3<Integer, Integer, Integer> IMPORT_PROGRESS = (percent, processed, total) -> prefixed(text()
            // "&b(Import) &b-> &f{}&f% complete &7- &b{}&f/&b{} &foperations complete."
            .append(text()
                    .color(AQUA)
                    .append(OPEN_BRACKET)
                    .append(translatable("command.import.term"))
                    .append(CLOSE_BRACKET)
            )
            .append(text(" -> ", AQUA))
            .append(translatable("command.import.progress.percent", WHITE, text(percent)))
            .append(text(" - ", GRAY))
            .append(translatable()
                    .key("command.import.progress.operations")
                    .color(WHITE)
                    .args(text(processed, AQUA), text(total, AQUA))
                    .append(FULL_STOP)
            )
    );

    Args0 IMPORT_START = () -> prefixed(text()
            // "&b(Import) &b-> &fStarting import process."
            .color(WHITE)
            .append(text()
                    .color(AQUA)
                    .append(OPEN_BRACKET)
                    .append(translatable("command.import.term"))
                    .append(CLOSE_BRACKET)
            )
            .append(text(" -> ", AQUA))
            .append(translatable("command.import.starting"))
            .append(FULL_STOP)
    );

    Args1<String> IMPORT_INFO = msg -> prefixed(text()
            // "&b(Import) &b-> &f{}."
            .color(WHITE)
            .append(text()
                    .color(AQUA)
                    .append(OPEN_BRACKET)
                    .append(translatable("command.import.term"))
                    .append(CLOSE_BRACKET)
            )
            .append(text(" -> ", AQUA))
            .append(text(msg))
            .append(FULL_STOP)
    );

    Args1<Double> IMPORT_END_COMPLETE = seconds -> prefixed(text()
            // "&b(Import) &a&lCOMPLETED &7- took &b{} &7seconds."
            .color(GRAY)
            .append(text()
                    .color(AQUA)
                    .append(OPEN_BRACKET)
                    .append(translatable("command.import.term"))
                    .append(CLOSE_BRACKET)
            )
            .append(space())
            .append(translatable("command.import.completed", GREEN, BOLD))
            .append(text(" - "))
            .append(translatable("command.import.duration", text(seconds, AQUA)))
            .append(FULL_STOP)
    );

    static Component formatContextBracketed(String key, String value) {
        // &8(&7{}=&f{}&8)
        return text()
                .color(DARK_GRAY)
                .append(OPEN_BRACKET)
                .append(text(key, GRAY))
                .append(text('=', GRAY))
                .append(text(value, WHITE))
                .append(CLOSE_BRACKET)
                .build();
    }

    static Component formatContextBracketed(Context context) {
        return formatContextBracketed(context.getKey(), context.getValue());
    }

    static Component formatContext(String key, String value) {
        // "&3{}=&b{}"
        return text()
                .content(key)
                .color(DARK_AQUA)
                .append(text('='))
                .append(text(value, AQUA))
                .build();
    }

    static Component formatContext(Context context) {
        return formatContext(context.getKey(), context.getValue());
    }

    static Component formatContextSet(ContextSet set) {
        // "&3server=&bsurvival&a, &3world=&bnether"
        Iterator<Context> it = set.iterator();
        if (!it.hasNext()) {
            return text("global", YELLOW); // "&eglobal"
        }

        TextComponent.Builder builder = text().color(GREEN);

        builder.append(formatContext(it.next()));
        while (it.hasNext()) {
            builder.append(text(", "));
            builder.append(formatContext(it.next()));
        }

        return builder.build();
    }

    static Component formatContextSetBracketed(ContextSet set, Component ifEmpty) {
        // "&8(&7server=&fsurvival&8) &8(&7world=&fnether&8)"
        Iterator<Context> it = set.iterator();
        if (!it.hasNext()) {
            return ifEmpty;
        }

        TextComponent.Builder builder = text();

        builder.append(formatContextBracketed(it.next()));
        while (it.hasNext()) {
            builder.append(text(" "));
            builder.append(formatContextBracketed(it.next()));
        }

        return builder.build();
    }

    static Component formatTrackPath(List<String> groups)  {
        Iterator<String> it = groups.iterator();
        if (!it.hasNext()) {
            return translatable("command.track.path.empty", GOLD); // "&6None"
        }

        TextComponent.Builder builder = text().color(DARK_AQUA).content(it.next());

        while (it.hasNext()) {
            builder.append(text(" ---> ", AQUA));
            builder.append(text(it.next()));
        }

        return builder.build();
    }

    static Component formatTrackPath(List<String> groups, String highlighed) {
        if (groups.isEmpty()) {
            return translatable("command.track.path.empty", AQUA); // "&bNone"
        }

        TextComponent.Builder builder = text().color(DARK_AQUA);

        boolean first = true;
        for (String group : groups) {
            if (first) {
                first = false;
            } else {
                builder.append(text(" ---> ", GRAY));
            }

            builder.append(group.equalsIgnoreCase(highlighed) ? text(group, AQUA) : text(group));
        }

        return builder.build();
    }

    static Component formatTrackPath(List<String> groups, String highlightedFirst, String highlightedSecond, boolean reversed) {
        if (groups.isEmpty()) {
            return translatable("command.track.path.empty", GOLD); // "&6None"
        }

        TextComponent.Builder builder = text().color(DARK_AQUA);

        boolean first = true;
        boolean highlight = false;

        for (String group : groups) {
            if (first) {
                first = false;
            } else {
                builder.append(text(reversed ? " <--- " : " ---> ", highlight ? DARK_RED : GRAY));
            }

            if (group.equalsIgnoreCase(highlightedFirst)) {
                builder.append(text(group, AQUA));
                highlight = true;
            } else if (group.equalsIgnoreCase(highlightedSecond)) {
                builder.append(text(group, AQUA));
                highlight = false;
            } else {
                builder.append(text(group));
            }
        }

        return builder.build();
    }

    static Component formatStringList(Collection<String> strings) {
        Iterator<String> it = strings.iterator();
        if (!it.hasNext()) {
            return translatable("command.misc.none", AQUA); // "&bNone"
        }

        TextComponent.Builder builder = text().color(DARK_AQUA).content(it.next());

        while (it.hasNext()) {
            builder.append(text(", ", GRAY));
            builder.append(text(it.next()));
        }

        return builder.build();
    }

    static Component formatBoolean(boolean bool) {
        return bool ? text("true", GREEN) : text("false", RED);
    }

    static Component formatTristate(Tristate tristate) {
        switch (tristate) {
            case TRUE:
                return text("true", GREEN);
            case FALSE:
                return text("false", RED);
            default:
                return text("undefined", GRAY);
        }
    }

    interface Args0 {
        Component build();

        default void send(Sender sender) {
            sender.sendMessage(build());
        }
    }

    interface Args1<A0> {
        Component build(A0 arg0);

        default void send(Sender sender, A0 arg0) {
            sender.sendMessage(build(arg0));
        }
    }

    interface Args2<A0, A1> {
        Component build(A0 arg0, A1 arg1);

        default void send(Sender sender, A0 arg0, A1 arg1) {
            sender.sendMessage(build(arg0, arg1));
        }
    }

    interface Args3<A0, A1, A2> {
        Component build(A0 arg0, A1 arg1, A2 arg2);

        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2) {
            sender.sendMessage(build(arg0, arg1, arg2));
        }
    }

    interface Args4<A0, A1, A2, A3> {
        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3);

        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3));
        }
    }

    interface Args5<A0, A1, A2, A3, A4> {
        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4);

        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3, arg4));
        }
    }

    interface Args6<A0, A1, A2, A3, A4, A5> {
        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);

        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3, arg4, arg5));
        }
    }

}
